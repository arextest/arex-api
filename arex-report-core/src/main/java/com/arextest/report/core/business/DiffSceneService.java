package com.arextest.report.core.business;

import com.arextest.report.core.repository.ReportDiffAggStatisticRepository;
import com.arextest.report.model.api.contracts.QueryDifferencesRequestType;
import com.arextest.report.model.api.contracts.QueryDifferencesResponseType;
import com.arextest.report.model.api.contracts.QueryScenesRequestType;
import com.arextest.report.model.api.contracts.QueryScenesResponseType;
import com.arextest.report.model.api.contracts.common.Difference;
import com.arextest.report.model.api.contracts.common.Scene;
import com.arextest.report.model.dto.DifferenceDto;
import com.arextest.report.model.dto.SceneDto;
import com.arextest.report.model.mapper.DifferenceMapper;
import com.arextest.report.model.mapper.SceneMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DiffSceneService {
    @Resource
    ReportDiffAggStatisticRepository reportDiffAggStatisticRepository;

    public QueryDifferencesResponseType queryDifferences(QueryDifferencesRequestType request) {
        QueryDifferencesResponseType response = new QueryDifferencesResponseType();

        List<DifferenceDto> dtos = reportDiffAggStatisticRepository.queryDifferences(request.getPlanItemId(),
                request.getCategoryName(), request.getOperationName());
        List<Difference> diffs = new ArrayList<>();
        dtos.forEach(dto -> {
            diffs.add(DifferenceMapper.INSTANCE.contractFromDto(dto));
        });
        response.setDifferences(diffs);
        return response;
    }

    public QueryScenesResponseType queryScenesByPage(QueryScenesRequestType request) {
        QueryScenesResponseType response = new QueryScenesResponseType();

        List<SceneDto> sceneDtos = reportDiffAggStatisticRepository.queryScenesByDifference(request.getPlanItemId(),
                request.getCategoryName(), request.getOperationName(), request.getDifferenceName());
        List<Scene> scenes = new ArrayList<>();
        sceneDtos.forEach(dto -> {
            scenes.add(SceneMapper.INSTANCE.contractFromDto(dto));
        });
        response.setScenes(scenes);
        return response;
    }
}
