package com.arextest.web.core.business.iosummary;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.model.contract.contracts.QuerySceneInfoResponseType;
import com.arextest.web.model.dao.mongodb.ReplayCompareResultCollection;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.dto.iosummary.SubSceneInfo;
import com.arextest.web.model.mapper.SceneInfoMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
     * from CaseSummary to ScnenInfo
     */
    public void report(CaseSummary caseSummary) {
        SceneInfo sceneInfo = SceneInfo.builder()
                .code(caseSummary.getCode())
                .categoryKey(caseSummary.categoryKey())
                .planId(caseSummary.getPlanId())
                .planItemId(caseSummary.getPlanItemId())
                .summary(caseSummary).build();
        SceneInfo savedSceneInfo = sceneInfoRepository.save(sceneInfo);
        /**
         * determine whether to compare according to the count number
         * If count=1, trigger comparison
         * If cunnt is greater than 1, no comparison is made
         */
        if (shouldCompare(caseSummary, savedSceneInfo)) {

            // String planId = caseSummary.getPlanId();
            // String planItemId = caseSummary.getPlanItemId();
            // String recordId = caseSummary.getRecordId();
            //
            // List<CompareResultDto> dtos = replayCompareResultRepository.queryCompareResults(planId,
            //         Collections.singletonList(planItemId),
            //         Collections.singletonList(recordId),
            //         null,
            //         Arrays.asList(
            //                 ReplayCompareResultCollection.Fields.baseMsg,
            //                 ReplayCompareResultCollection.Fields.testMsg
            //         )
            // );
            // if (CollectionUtils.isEmpty(dtos)) {
            //     LogUtils.warn(LOGGER, "report don't find compareResult",
            //             ImmutableMap.of("planId", planId, "recordId", recordId));
            // } else {
            //     CompareResultDto dto = dtos.get(0);
            //     // todo: getCompareConfig
            //     // 全量触发落库
            //
            // }


        }
    }

    public QuerySceneInfoResponseType querySceneInfo(String planId, String planItemId) {
        QuerySceneInfoResponseType response = new QuerySceneInfoResponseType();
        List<SceneInfo> sceneInfos = sceneInfoRepository.querySceneInfo(planId, planItemId);
        List<QuerySceneInfoResponseType.SceneInfoType> sceneInfoTypes =
                sceneInfos.stream()
                        .map(SceneInfoMapper.INSTANCE::contractFromDto)
                        .collect(Collectors.toList());

        // to set recordTime and replayTime
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

    private boolean shouldCompare(CaseSummary caseSummary, SceneInfo sceneInfo) {
        String categoryKey = String.valueOf(caseSummary.groupKey());
        Map<String, SubSceneInfo> subSceneInfoMap = sceneInfo.getSubSceneInfoMap();
        if (subSceneInfoMap != null && subSceneInfoMap.containsKey(categoryKey)) {
            SubSceneInfo subSceneInfo = subSceneInfoMap.get(categoryKey);
            if (subSceneInfo.getCode() == 1) {
                return true;
            }
        }
        return false;
    }
}
