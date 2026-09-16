package com.codifyai.llm.tools;

import com.codifyai.service.ProjectFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public record CodeGenerationTools(
        ProjectFileService projectFileService,
        Long projectId
) {

    @Tool(
            name = "read_files",
            description = "Read the content of files. Only input file paths present inside the FILE_TREE. Do not request files that are not present in the FILE_TREE."
    )
    public List<String> readFiles(
            @ToolParam(
                    description = "List of relative file paths, for example ['src/App.tsx']"
            )
            List<String> paths
    ) {
        List<String> result = new ArrayList<>();

        for (String path : paths) {
            String cleanPath = cleanPath(path);

            log.info("Reading file: {}", cleanPath);

            String content = projectFileService
                    .getFileContent(projectId, cleanPath)
                    .content();

            result.add(String.format(
                    "--- START OF FILE: %s ---\n%s\n--- END OF FILE ---",
                    cleanPath,
                    content
            ));
        }

        return result;
    }

    @Tool(
            name = "write_file",
            description = "Create or overwrite a project file. Use this tool when you need to create or modify a file. The path must be relative to the project root."
    )
    public String writeFile(
            @ToolParam(
                    description = "Relative file path, for example src/App.tsx"
            )
            String path,

            @ToolParam(
                    description = "Complete content of the file. Never use placeholders or omit existing required code."
            )
            String content
    ) {
        String cleanPath = cleanPath(path);

        log.info("Writing file: {}", cleanPath);

        projectFileService.saveFile(projectId, cleanPath, content);

        return "File written successfully: " + cleanPath;
    }

    private String cleanPath(String path) {
        return path.startsWith("/")
                ? path.substring(1)
                : path;
    }
}