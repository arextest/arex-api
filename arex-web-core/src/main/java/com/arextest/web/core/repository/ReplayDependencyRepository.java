package com.arextest.web.core.repository;

import com.arextest.web.model.dto.ReplayDependencyDto;

public interface ReplayDependencyRepository extends RepositoryProvider {
    boolean saveDependency(ReplayDependencyDto replayDependency);

    ReplayDependencyDto queryDependency(String operationId);
}
