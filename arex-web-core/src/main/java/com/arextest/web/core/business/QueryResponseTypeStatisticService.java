package com.arextest.web.core.business;

import com.arextest.web.core.repository.mongo.ReplayCompareResultRepositoryImpl;
import com.arextest.web.model.contract.contracts.QueryCategoryStatisticRequestType;
import com.arextest.web.model.contract.contracts.QueryCategoryStatisticResponseType;
import com.arextest.web.model.contract.contracts.common.CategoryStatistic;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.enums.DiffResultCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
public class QueryResponseTypeStatisticService {

  @Resource
  private ReplayCompareResultRepositoryImpl replayCompareResultRepository;

  public QueryCategoryStatisticResponseType categoryStatistic(
      QueryCategoryStatisticRequestType request) {
    QueryCategoryStatisticResponseType response = new QueryCategoryStatisticResponseType();
    if (request == null || request.getPlanItemId() == null) {
      return response;
    }
    List<CompareResultDto> compareResultDtoList =
        replayCompareResultRepository.findResultWithoutMsg(request.getPlanItemId());
    if (CollectionUtils.isEmpty(compareResultDtoList)) {
      return response;
    }
    List<CategoryStatistic> categoryStatisticList = new ArrayList<>();
    Map<String, List<CompareResultDto>> categoryMap =
        compareResultDtoList.stream().filter(item -> item.getCategoryName() != null)
            .collect(Collectors.groupingBy(CompareResultDto::getCategoryName));
    categoryMap.forEach((categoryName, categoryMapValue) -> {
      if (CollectionUtils.isEmpty(categoryMapValue)) {
        return;
      }
      Map<String, List<CompareResultDto>> operationNameMap =
          categoryMapValue.stream().filter(item -> item.getOperationName() != null)
              .collect(Collectors.groupingBy(CompareResultDto::getOperationName));
      operationNameMap.forEach((operationName, operationNameMapValue) -> {
        if (CollectionUtils.isEmpty(operationNameMapValue)) {
          return;
        }
        Map<Integer,
            List<CompareResultDto>> resultTypeMap = operationNameMapValue.stream()
            .filter(
                item -> item.getDiffResultCode() != null && Strings.isNotBlank(item.getRecordId()))
            .collect(Collectors.groupingBy(CompareResultDto::getDiffResultCode));
        if (resultTypeMap == null) {
          return;
        }
        CategoryStatistic temp = new CategoryStatistic();
        temp.setCategoryName(categoryName);
        temp.setOperationName(operationName);
        temp.setTotalCaseCount(getCaseCount(operationNameMapValue));
        temp.setSuccessCaseCount(
            getCaseCount(resultTypeMap.get(DiffResultCode.COMPARED_WITHOUT_DIFFERENCE)));
        temp.setFailCaseCount(
            getCaseCount(resultTypeMap.get(DiffResultCode.COMPARED_WITH_DIFFERENCE)));
        temp.setErrorCaseCount(
            getCaseCount(resultTypeMap.get(DiffResultCode.COMPARED_INTERNAL_EXCEPTION)));
        categoryStatisticList.add(temp);
      });
    });
    response.setCategoryStatisticList(categoryStatisticList);
    return response;
  }

  private int getCaseCount(List<CompareResultDto> resultTypeMapValue) {
    if (CollectionUtils.isEmpty(resultTypeMapValue)) {
      return 0;
    }
    return (int) resultTypeMapValue.stream().map(CompareResultDto::getRecordId).count();
  }
}
