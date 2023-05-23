package com.arextest.web.core.business.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.model.contract.contracts.QuerySceneInfoResponseType;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.mapper.SceneInfoMapper;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SceneReportService {

    private static final String RECORD_ID = "recordId";
    private static final String RECORD_TIME = "recordTime";
    private static final String REPLAY_TIME = "replayTime";

    @Autowired
    CaseSummaryRepository caseSummaryRepository;

    @Autowired
    SceneInfoRepository sceneInfoRepository;

    @Autowired
    ReplayCompareResultRepository replayCompareResultRepository;

    /**
     * difference Type Scene Grouping
     */
    public void report(String planId, String planItemId) {
        List<CaseSummary> data = caseSummaryRepository.query(planId, planItemId);
        if (CollectionUtil.isEmpty(data)) {
            return;
        }
        List<SceneInfo> sceneInfos = groupMainScene(data).stream()
                .map(SceneInfo.Builder::build)
                .sorted(Comparator.comparingInt(SceneInfo::getCode))
                .collect(Collectors.toList());
        sceneInfoRepository.save(sceneInfos);
    }

    public QuerySceneInfoResponseType querySceneInfo(String planId, String planItemId) {
        QuerySceneInfoResponseType response = new QuerySceneInfoResponseType();
        List<SceneInfo> sceneInfos = sceneInfoRepository.querySceneInfo(planId, planItemId);
        List<QuerySceneInfoResponseType.SceneInfoType> sceneInfoTypes =
                sceneInfos.stream()
                        .map(SceneInfoMapper.INSTANCE::contractFromDto)
                        .collect(Collectors.toList());
        Map<String, QuerySceneInfoResponseType.SubSceneInfoType> subSceneInfoTypeMap =
                new HashMap<>();
        for (QuerySceneInfoResponseType.SceneInfoType sceneInfoType : sceneInfoTypes) {
            List<QuerySceneInfoResponseType.SubSceneInfoType> subScenes = sceneInfoType.getSubScenes();
            for (QuerySceneInfoResponseType.SubSceneInfoType subSceneInfoType : subScenes) {
                subSceneInfoTypeMap.put(subSceneInfoType.getRecordId(), subSceneInfoType);
            }
        }
        if (MapUtils.isNotEmpty(subSceneInfoTypeMap)) {
            List<CompareResultDto> dtos = replayCompareResultRepository.queryCompareResults(
                    planId,
                    Collections.singletonList(planItemId),
                    new ArrayList<>(subSceneInfoTypeMap.keySet()),
                    null,
                    Arrays.asList(RECORD_ID, RECORD_TIME, REPLAY_TIME)
            );
            for (CompareResultDto dto : dtos) {
                String recordId = dto.getRecordId();
                QuerySceneInfoResponseType.SubSceneInfoType subSceneInfoType = subSceneInfoTypeMap.get(recordId);
                if (subSceneInfoType != null) {
                    subSceneInfoType.setRecordTime(dto.getRecordTime());
                    subSceneInfoType.setReplayTime(dto.getReplayTime());
                }
            }
        }
        response.setSceneInfos(sceneInfoTypes);
        return response;
    }

    /**
     * Group main scenes according to Code
     */
    private Collection<SceneInfo.Builder> groupMainScene(List<CaseSummary> caseSummaries) {
        Map<Long, SceneInfo.Builder> main = new HashMap<>();
        for (CaseSummary summary : caseSummaries) {
            main.computeIfAbsent(summary.categoryKey(), k -> SceneInfo.builder()
                    .code(summary.getCode())
                    .planId(summary.getPlanId())
                    .planItemId(summary.getPlanItemId()))
                    .summary(summary);
        }
        return main.values();
    }
}
