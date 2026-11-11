package com.codifyai.controller;

import com.codifyai.dto.project.ProjectRequest;
import com.codifyai.service.DeploymentService;
import com.codifyai.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final DeploymentService deploymentService;

    @GetMapping
    public ResponseEntity<?> getMyProjects() {
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getUserProjectById(id));
    }

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        projectService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deploy")
    public ResponseEntity<?> deployProject(@PathVariable Long id) {
        return ResponseEntity.ok(deploymentService.deploy(id));
    }

}
