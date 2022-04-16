package io.arex.report.core.business;

import cn.hutool.core.collection.CollUtil;
import io.arex.report.core.repository.ReplayCompareResultRepository;
import io.arex.report.model.api.contracts.QueryReplayCaseRequestType;
import io.arex.report.model.api.contracts.QueryReplayCaseResponseType;
import io.arex.report.model.api.contracts.common.CaseDetailResult;
import io.arex.report.model.dto.CompareResultDto;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class QueryReplayCaseService {

    @Resource
    private ReplayCompareResultRepository replayCompareResultRepository;

    public QueryReplayCaseResponseType replayCaseStatistic(QueryReplayCaseRequestType request) {
        QueryReplayCaseResponseType response = new QueryReplayCaseResponseType();

        List<CompareResultDto> resultWithoutMsg = replayCompareResultRepository.findResultWithoutMsg(request.getPlanItemId(),
                request.getKeyWord());

        Map<Pair<String,String>, List<CompareResultDto>> resultCaseMap =
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
        if (Boolean.TRUE.equals(request.getNeedTotal())){
            response.setTotalCount(Long.valueOf(results.size()));
        }
        response.setResult(caseDetailResults);
        return response;
    }
}
