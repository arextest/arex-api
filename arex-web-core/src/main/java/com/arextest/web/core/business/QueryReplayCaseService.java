package com.arextest.web.core.business;

import cn.hutool.core.collection.CollUtil;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.model.contract.contracts.QueryPlanFailCaseRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanFailCaseResponseType;
import com.arextest.web.model.contract.contracts.QueryReplayCaseRequestType;
import com.arextest.web.model.contract.contracts.QueryReplayCaseResponseType;
import com.arextest.web.model.contract.contracts.common.CaseDetailResult;
import com.arextest.web.model.dto.CompareResultDto;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class QueryReplayCaseService {

    @Resource
    private ReplayCompareResultRepository replayCompareResultRepository;

    public QueryReplayCaseResponseType replayCaseStatistic(QueryReplayCaseRequestType request) {
        QueryReplayCaseResponseType response = new QueryReplayCaseResponseType();

        List<CompareResultDto> resultWithoutMsg = replayCompareResultRepository.findResultWithoutMsg(request.getPlanItemId(),
                request.getKeyWord());

        Map<Pair<String, String>, List<CompareResultDto>> resultCaseMap =
                resultWithoutMsg.stream().collect(Collectors.groupingBy(e -> new MutablePair<>(e.getRecordId(), e.getReplayId())));
        List<CaseDetailResult> results = new ArrayList<>();
        resultCaseMap.forEach((key, resultCaseList) -> {
            CaseDetailResult caseDetail = new CaseDetailResult();
            caseDetail.setRecordId(key.getLeft());
            caseDetail.setReplayId(key.getRight());
            caseDetail.setDiffResultCode(Collections.max(resultCaseList, Comparator.comparing(CompareResultDto::getDiffResultCode)).getDiffResultCode());
            results.add(caseDetail);
        });


        if (request.getDiffResultCode() != null) {
            List<CaseDetailResult> finalResults = new ArrayList<>();
            for (CaseDetailResult caseDetail : results) {
                if (caseDetail.getDiffResultCode().equals(request.getDiffResultCode())) {
                    finalResults.add(caseDetail);
                }
            }
            results.clear();
            results.addAll(finalResults);
            finalResults.clear();
        }

        results.sort((m1, m2) -> m2.getDiffResultCode().compareTo(m1.getDiffResultCode()));
        List<CaseDetailResult> caseDetailResults = CollUtil.sortPageAll(request.getPageIndex() - 1, request.getPageSize(), null, results);
        if (Boolean.TRUE.equals(request.getNeedTotal())) {
            response.setTotalCount(Long.valueOf(results.size()));
        }
        response.setResult(caseDetailResults);
        return response;
    }


    public QueryPlanFailCaseResponseType queryPlanFailCase(QueryPlanFailCaseRequestType request) {
        QueryPlanFailCaseResponseType response = new QueryPlanFailCaseResponseType();
        List<QueryPlanFailCaseResponseType.FailCaseInfo> failCaseInfoList = new ArrayList<>();
        List<CompareResultDto> dtos = replayCompareResultRepository.queryFailCompareResults(
                request.getPlanId(),
                request.getPlanItemIdList(),
                request.getRecordIdList(),
                request.getDiffResultCodeList());
        Map<String, List<CompareResultDto>> compareResultDtoMap = dtos.stream()
                .collect(Collectors.groupingBy(CompareResultDto::getOperationId));
        for (Map.Entry<String, List<CompareResultDto>> entry : compareResultDtoMap.entrySet()) {
            String operationId = entry.getKey();
            List<CompareResultDto> compareResultDtoList = entry.getValue();
            QueryPlanFailCaseResponseType.FailCaseInfo failCaseInfo
                    = new QueryPlanFailCaseResponseType.FailCaseInfo();
            failCaseInfo.setOperationId(operationId);
            List<String> recordIdList = compareResultDtoList.stream()
                    .map(CompareResultDto::getRecordId)
                    .collect(Collectors.toList());
            failCaseInfo.setReplayIdList(recordIdList);
            failCaseInfoList.add(failCaseInfo);
        }
        response.setFailCaseInfoList(failCaseInfoList);
        return response;
    }
}
