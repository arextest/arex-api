package com.arextest.web.core.business;

import com.arextest.web.core.repository.ReportDiffAggStatisticRepository;
import com.arextest.web.model.contract.contracts.QueryDifferencesRequestType;
import com.arextest.web.model.contract.contracts.QueryDifferencesResponseType;
import com.arextest.web.model.contract.contracts.QueryScenesRequestType;
import com.arextest.web.model.contract.contracts.QueryScenesResponseType;
import com.arextest.web.model.contract.contracts.common.Difference;
import com.arextest.web.model.contract.contracts.common.Scene;
import com.arextest.web.model.dto.DifferenceDto;
import com.arextest.web.model.dto.SceneDto;
import com.arextest.web.model.mapper.DifferenceMapper;
import com.arextest.web.model.mapper.SceneMapper;
import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiffSceneService {

  @Resource
  ReportDiffAggStatisticRepository reportDiffAggStatisticRepository;

  public QueryDifferencesResponseType queryDifferences(QueryDifferencesRequestType request) {
    QueryDifferencesResponseType response = new QueryDifferencesResponseType();

    List<DifferenceDto> dtos = reportDiffAggStatisticRepository.queryDifferences(
        request.getPlanItemId(),
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

    List<SceneDto> sceneDtos = reportDiffAggStatisticRepository.queryScenesByDifference(
        request.getPlanItemId(),
        request.getCategoryName(), request.getOperationName(), request.getDifferenceName());
    List<Scene> scenes = new ArrayList<>();
    sceneDtos.forEach(dto -> {
      scenes.add(SceneMapper.INSTANCE.contractFromDto(dto));
    });
    response.setScenes(scenes);
    return response;
  }
}
