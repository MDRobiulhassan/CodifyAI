package com.codifyai.service;

import com.codifyai.dto.deploy.DeployResponse;

public interface DeploymentService {
    DeployResponse deploy(Long projectId);
}
