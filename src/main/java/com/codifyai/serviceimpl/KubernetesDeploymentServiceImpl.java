package com.codifyai.serviceimpl;

import com.codifyai.dto.deploy.DeployResponse;
import com.codifyai.service.DeploymentService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesDeploymentServiceImpl implements DeploymentService {

    private static final String NAMESPACE = "codify-apps";
    private static final String POOL_LABEL = "status";
    private static final String PROJECT_LABEL = "project-id";
    private static final String IDLE = "idle";
    private static final String BUSY = "busy";
    private static final String SYNCER_CONTAINER = "syncer";
    private static final String RUNNER_CONTAINER = "runner";
    private static final String REVERSE_PROXY_PORT = "8090";
    private final KubernetesClient client;

    @Override
    public DeployResponse deploy(Long projectId) {
        String domain = "project-" + projectId + ".app.codify-ai.com";
        Pod existingPod = findActivePods(projectId);

        if (existingPod != null)
            return new DeployResponse("https://" + domain + ":" + REVERSE_PROXY_PORT);

        return claimAndStartNewPod(projectId, domain);
    }

    private DeployResponse claimAndStartNewPod(Long projectId, String domain) {
        Pod pod = client.pods().inNamespace(NAMESPACE)
                .withLabel(PROJECT_LABEL, IDLE)
                .list().getItems().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No idle runners available.Please scale up the runner pool."));

        String podName = pod.getMetadata().getName();
        log.info("Claiming pod {} for project {}", podName, projectId);

        client.pods().inNamespace(NAMESPACE).withName(podName).edit(p -> {
            p.getMetadata().getLabels().put(PROJECT_LABEL, BUSY);
            p.getMetadata().getLabels().put(POOL_LABEL, projectId.toString());
            return p;
        });

        try {
            String initialSyncCmd = String.format("rm -rf /app* && mc mirror --overwrite myminio/projects/%d /app/", projectId);

            log.info("Starting initial sync for project {} in pod {}", projectId, podName);
            execCommand(podName, SYNCER_CONTAINER, "sh", "-c", initialSyncCmd);

            String watchCmd = String.format("nohup mc mirror --overwrite --watch myminio/projects/%d /app/ > /app/sync.log 2>&1 &", projectId);
            execCommand(podName, "syncer", "sh", "-c", watchCmd);

            String startCmd = "npm install && nohup npm run dev -- --host 0.0.0.0 --port 5173 > /app/dev.log 2>&1 &";
            log.info("Starting dev server for project {}", projectId);
            execCommand(podName, RUNNER_CONTAINER, "sh", "-c", startCmd);

            log.info("Deployment successful: https://{}:{}", domain, REVERSE_PROXY_PORT);
            return new DeployResponse("https://" + domain + ":" + REVERSE_PROXY_PORT);
        } catch (Exception e) {
            log.error("Deployment failed for project {}", projectId, e);
            client.pods().inNamespace(NAMESPACE).withName(podName).delete();
            throw new RuntimeException("Failed to deploy project " + projectId + ". Please try again later.", e);
        }
    }

    private void execCommand(String podName, String container, String... command) {
        log.debug("Exec in {}:{}-> {}", podName, container, String.join(" ", command));

        CompletableFuture<String> data = new CompletableFuture<>();
        try (ExecWatch ignored = client.pods().inNamespace(NAMESPACE).withName(podName)
                .inContainer(container)
                .writingOutput(new ByteArrayOutputStream())
                .writingError(new ByteArrayOutputStream())
                .usingListener(new ExecListener() {
                    @Override
                    public void onClose(int code, String reason) {
                        data.complete("Done");
                    }
                })
                .exec(command)) {

            if (command[command.length - 1].trim().endsWith("&")) {
                Thread.sleep(500);
            } else {
                data.get(30, TimeUnit.SECONDS);
            }

        } catch (Exception e) {
            log.error("Exec Failed", e);
            throw new RuntimeException("Pod Execution Failed", e);
        }
    }

    private Pod findActivePods(Long projectId) {
        return client.pods().inNamespace(NAMESPACE)
                .withLabel(PROJECT_LABEL, projectId.toString())
                .withLabel(POOL_LABEL, BUSY)
                .list().getItems().stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .findFirst()
                .orElse(null);
    }
}
