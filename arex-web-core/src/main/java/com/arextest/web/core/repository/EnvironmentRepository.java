package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.EnvironmentDto;

public interface EnvironmentRepository extends RepositoryProvider {

    EnvironmentDto initEnvironment(EnvironmentDto environment);

    EnvironmentDto saveEnvironment(EnvironmentDto environment);

    boolean removeEnvironment(String id);

    List<EnvironmentDto> queryEnvsByWorkspace(String workspaceId);

    EnvironmentDto queryById(String id);
}
