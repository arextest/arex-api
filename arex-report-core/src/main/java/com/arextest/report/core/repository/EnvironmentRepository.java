package com.arextest.report.core.repository;


import com.arextest.report.model.api.contracts.environment.SaveEnvironmentResponseType;
import com.arextest.report.model.dto.EnvironmentDto;

import java.util.List;

public interface EnvironmentRepository extends RepositoryProvider {

    EnvironmentDto initEnvironment(EnvironmentDto environment);

    EnvironmentDto saveEnvironment(EnvironmentDto environment);

    boolean removeEnvironment(String id);

    List<EnvironmentDto> queryEnvsByWorkspace(String workspaceId);

    EnvironmentDto queryById(String id);
}
