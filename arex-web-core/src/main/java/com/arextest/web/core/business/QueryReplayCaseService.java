package com.arextest.web.core.business;

import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.model.contract.contracts.QueryPlanFailCaseRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanFailCaseResponseType;
import com.arextest.web.model.contract.contracts.QueryReplayCaseRequestType;
import com.arextest.web.model.contract.contracts.QueryReplayCaseResponseType;
import com.arextest.web.model.contract.contracts.common.CaseDetailResult;
import com.arextest.web.model.dto.CompareResultDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

@Component
public class QueryReplayCaseService {

  private static final String OPERATION_ID = "operationId";
  private static final String RECORD_ID = "recordId";

  @Resource
  private ReplayCompareResultRepository replayCompareResultRepository;

  public QueryReplayCaseResponseType replayCaseStatistic(QueryReplayCaseRequestType request) {
    QueryReplayCaseResponseType response = new QueryReplayCaseResponseType();
      if (BooleanUtils.isTrue(request.getNeedTotal())) {
          response.setTotalCount(
              replayCompareResultRepository.countWithDistinct(request.getPlanItemId(), request.getDiffResultCode(),
                  request.getKeyWord()));
      }

    List<CompareResultDto> resultList = replayCompareResultRepository.findResultWithoutMsg(request);
    List<CaseDetailResult> results = resultList.stream().map(item-> {
      CaseDetailResult caseDetail = new CaseDetailResult();
      caseDetail.setCaseId(item.getCaseId());
      caseDetail.setRecordId(item.getRecordId());
      caseDetail.setReplayId(item.getReplayId());
      caseDetail.setDiffResultCode(item.getDiffResultCode());
      return caseDetail;
    }).collect(Collectors.toList());
    response.setResult(results);
    return response;
  }

  public QueryPlanFailCaseResponseType queryPlanFailCase(QueryPlanFailCaseRequestType request) {
    QueryPlanFailCaseResponseType response = new QueryPlanFailCaseResponseType();
    List<QueryPlanFailCaseResponseType.FailCaseInfo> failCaseInfoList = new ArrayList<>();
    List<Integer> diffResultCodeList = new ArrayList<>();
    if (CollectionUtils.isEmpty(request.getRecordIdList())
        && CollectionUtils.isNotEmpty(request.getDiffResultCodeList())) {
      diffResultCodeList.addAll(request.getDiffResultCodeList());
    }
    List<CompareResultDto> dtos =
        replayCompareResultRepository.queryCompareResults(request.getPlanId(),
            request.getPlanItemIdList(),
            request.getRecordIdList(), diffResultCodeList, Arrays.asList(OPERATION_ID, RECORD_ID));
    Map<String, List<CompareResultDto>> compareResultDtoMap =
        dtos.stream().collect(Collectors.groupingBy(CompareResultDto::getOperationId));
    for (Map.Entry<String, List<CompareResultDto>> entry : compareResultDtoMap.entrySet()) {
      String operationId = entry.getKey();
      List<CompareResultDto> compareResultDtoList = entry.getValue();
      QueryPlanFailCaseResponseType.FailCaseInfo failCaseInfo = new QueryPlanFailCaseResponseType.FailCaseInfo();
      failCaseInfo.setOperationId(operationId);
      Set<String> recordIdList =
          compareResultDtoList.stream().map(CompareResultDto::getRecordId)
              .collect(Collectors.toSet());
      failCaseInfo.setReplayIdList(recordIdList);
      failCaseInfoList.add(failCaseInfo);
    }
    response.setFailCaseInfoList(failCaseInfoList);
    return response;
  }
}
