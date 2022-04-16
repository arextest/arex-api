package io.arex.report.core.repository;

import io.arex.report.model.dto.DiffAggDto;
import io.arex.report.model.dto.DifferenceDto;
import io.arex.report.model.dto.SceneDto;

import java.util.List;


public interface ReportDiffAggStatisticRepository extends RepositoryProvider {
    DiffAggDto updateDiffScenes(DiffAggDto dto);

    List<DifferenceDto> queryDifferences(Long planItemId, String categoryName, String operationName);

    List<SceneDto> queryScenesByDifference(Long planItemId,
            String categoryName,
            String operationName,
            String diffName);
}
