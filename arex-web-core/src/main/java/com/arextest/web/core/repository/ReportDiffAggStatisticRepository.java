package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.DiffAggDto;
import com.arextest.web.model.dto.DifferenceDto;
import com.arextest.web.model.dto.SceneDto;

public interface ReportDiffAggStatisticRepository extends RepositoryProvider {
    DiffAggDto updateDiffScenes(DiffAggDto dto);

    List<DifferenceDto> queryDifferences(String planItemId, String categoryName, String operationName);

    List<SceneDto> queryScenesByDifference(String planItemId, String categoryName, String operationName,
        String diffName);

    boolean deleteDiffAggByPlanId(String planId);
}
