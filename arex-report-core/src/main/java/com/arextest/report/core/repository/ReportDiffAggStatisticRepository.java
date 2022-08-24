package com.arextest.report.core.repository;

import com.arextest.report.model.dto.DiffAggDto;
import com.arextest.report.model.dto.DifferenceDto;
import com.arextest.report.model.dto.SceneDto;

import java.util.List;


public interface ReportDiffAggStatisticRepository extends RepositoryProvider {
    DiffAggDto updateDiffScenes(DiffAggDto dto);

    List<DifferenceDto> queryDifferences(String planItemId, String categoryName, String operationName);

    List<SceneDto> queryScenesByDifference(String planItemId,
            String categoryName,
            String operationName,
            String diffName);
}
