package com.arextest.web.core.business.iosummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.core.repository.ReportPlanItemStatisticRepository;
import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.model.contract.contracts.FeedbackSceneRequest;
import com.arextest.web.model.contract.contracts.QuerySceneInfoResponseType;
import com.arextest.web.model.contract.contracts.RemoveRecordsAndScenesRequest;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.PlanItemDto;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.dto.iosummary.SubSceneInfo;
import com.arextest.web.model.enums.DiffResultCode;
import com.arextest.web.model.enums.FeedbackTypeEnum;
import com.arextest.web.model.mapper.SceneInfoMapper;

import lombok.extern.slf4j.Slf4j;

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

    @Autowired
    ReportPlanItemStatisticRepository reportPlanItemStatisticRepository;

    /**
     * from CaseSummary to ScnenInfo
     */
    public void report(CaseSummary caseSummary) {
        SceneInfo sceneInfo = SceneInfo.builder().code(caseSummary.getCode()).categoryKey(caseSummary.categoryKey())
            .planId(caseSummary.getPlanId()).planItemId(caseSummary.getPlanItemId()).summary(caseSummary).build();
        sceneInfoRepository.save(sceneInfo);
    }

    /**
     * merge sceneInfo with same categoryKey.
     */
    private List<SceneInfo> checkDuplicateScene(List<SceneInfo> sceneInfos) {
        Map<Long, SceneInfo> sceneInfoMap = new HashMap<>();
        Set<String> removeSceneIds = new HashSet<>();
        List<SceneInfo> updateSceneInfoList = new ArrayList<>();
        for (SceneInfo sceneInfo : sceneInfos) {
            SceneInfo existedScene = sceneInfoMap.get(sceneInfo.getCategoryKey());
            if (existedScene != null) {
                removeSceneIds.add(sceneInfo.getId());
                removeSceneIds.add(existedScene.getId());
                existedScene.getSubSceneInfoMap().putAll(sceneInfo.getSubSceneInfoMap());
                existedScene.setCount(existedScene.getCount() + sceneInfo.getCount());
                updateSceneInfoList.add(existedScene);
            } else {
                sceneInfoMap.put(sceneInfo.getCategoryKey(), sceneInfo);
            }
        }
        List<SceneInfo> newSceneInfos = new ArrayList<>(sceneInfoMap.values());
        if (!CollectionUtils.isEmpty(removeSceneIds)) {
            LOGGER.info("merge sceneInfo with same categoryKey, mergedSceneIds:{}", removeSceneIds);
            sceneInfoRepository.removeById(removeSceneIds);
            sceneInfoRepository.save(updateSceneInfoList);
        }
        return newSceneInfos;
    }

    public QuerySceneInfoResponseType querySceneInfo(String planId, String planItemId) {
        QuerySceneInfoResponseType response = new QuerySceneInfoResponseType();
        List<SceneInfo> sceneInfos = sceneInfoRepository.querySceneInfo(planId, planItemId);
        sceneInfos = checkDuplicateScene(sceneInfos);
        List<QuerySceneInfoResponseType.SceneInfoType> sceneInfoTypes =
            sceneInfos.stream().map(SceneInfoMapper.INSTANCE::contractFromDto).collect(Collectors.toList());

        // to set recordTime and replayTime
        Map<String, QuerySceneInfoResponseType.SubSceneInfoType> subSceneInfoTypeMap = new HashMap<>();
        for (QuerySceneInfoResponseType.SceneInfoType sceneInfoType : sceneInfoTypes) {
            List<QuerySceneInfoResponseType.SubSceneInfoType> subScenes = sceneInfoType.getSubScenes();
            for (QuerySceneInfoResponseType.SubSceneInfoType subSceneInfoType : subScenes) {
                subSceneInfoTypeMap.put(subSceneInfoType.getRecordId(), subSceneInfoType);
            }
        }
        if (MapUtils.isNotEmpty(subSceneInfoTypeMap)) {
            List<CompareResultDto> dtos = replayCompareResultRepository.queryCompareResults(planId,
                Collections.singletonList(planItemId), new ArrayList<>(subSceneInfoTypeMap.keySet()), null,
                Arrays.asList(RECORD_ID, RECORD_TIME, REPLAY_TIME));
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

    public boolean removeScene(RemoveRecordsAndScenesRequest request) {
        return sceneInfoRepository.removeByPlanItemId(request.getActionIdAndRecordIdsMap().keySet());
    }

    public boolean feedbackScene(FeedbackSceneRequest request) {
        final String planId = request.getPlanId();
        final String planItemId = request.getPlanItemId();
        final String recordId = request.getRecordId();
        final Integer feedbackType = request.getFeedbackType();

        // update scene
        List<SceneInfo> sceneInfos = sceneInfoRepository.querySceneInfo(planId, planItemId);
        sceneInfos.forEach(sceneInfo -> {
            if (MapUtils.isNotEmpty(sceneInfo.getSubSceneInfoMap())) {
                sceneInfo.getSubSceneInfoMap().forEach((groupKey, subSceneInfo) -> {
                    if (Objects.equals(subSceneInfo.getRecordId(), recordId)) {
                        subSceneInfo.setFeedbackType(feedbackType);
                        subSceneInfo.setRemark(request.getRemark());
                        if (Objects.equals(request.getFeedbackType(), FeedbackTypeEnum.BY_DESIGN.getCode())
                            || Objects.equals(request.getFeedbackType(), FeedbackTypeEnum.AREX_PROBLEM.getCode())) {
                            subSceneInfo.setCode(DiffResultCode.COMPARED_WITHOUT_DIFFERENCE);
                            passCases(planId, planItemId, groupKey);
                            if (checkAllSubScenes(sceneInfo)) {
                                sceneInfo.setCode(DiffResultCode.COMPARED_WITHOUT_DIFFERENCE);
                            }
                        }
                        sceneInfo.setReCalculated(true);
                        sceneInfoRepository.update(sceneInfo);
                    }
                });
            }
        });
        return true;
    }

    private boolean checkAllSubScenes(SceneInfo sceneInfo) {
        boolean result = true;
        for (SubSceneInfo subSceneInfo : sceneInfo.getSubSceneInfoMap().values()) {
            if (subSceneInfo.getCode() != DiffResultCode.COMPARED_WITHOUT_DIFFERENCE) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean passCases(String planId, String planItemId, String groupKey) {
        LOGGER.info("All the scenes has been passed");
        // query other cases in the same subScene
        List<CaseSummary> caseSummaryList = caseSummaryRepository.query(planId, planItemId);
        List<String> recordIds =
            caseSummaryList.stream().filter(caseSummary -> String.valueOf(caseSummary.groupKey()).equals(groupKey))
                .map(CaseSummary::getRecordId).collect(Collectors.toList());

        // update compareResult
        List<CompareResultDto> compareResultList = replayCompareResultRepository.queryCompareResults(planId,
            Collections.singletonList(planItemId), recordIds, null, Arrays.asList(RECORD_ID, RECORD_TIME, REPLAY_TIME));
        for (CompareResultDto compareResult : compareResultList) {
            compareResult.setDiffResultCode(DiffResultCode.COMPARED_WITHOUT_DIFFERENCE);
        }
        replayCompareResultRepository.updateResults(compareResultList);

        // update planItemStatistic
        PlanItemDto planItemDto = reportPlanItemStatisticRepository.findByPlanItemId(planItemId);
        if (planItemDto.getErrorCases() != null) {
            recordIds.forEach(itemRecordID -> planItemDto.getErrorCases().remove(itemRecordID));
        }
        if (planItemDto.getFailCases() != null) {
            recordIds.forEach(itemRecordID -> planItemDto.getFailCases().remove(itemRecordID));
        }

        return reportPlanItemStatisticRepository.findAndModifyCaseMap(planItemDto);
    }
}
