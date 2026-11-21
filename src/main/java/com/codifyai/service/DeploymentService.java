package com.codifyai.service;

import com.codifyai.dto.response.deploy.DeployResponse;

public interface DeploymentService {
    DeployResponse deploy(Long projectId);
}
