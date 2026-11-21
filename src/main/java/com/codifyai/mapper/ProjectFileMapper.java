package com.codifyai.mapper;

import com.codifyai.dto.response.project.FileNode;
import com.codifyai.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring" )
public interface ProjectFileMapper {
    List<FileNode> toListOfFileNode(List<ProjectFile> projectFiles);
}
