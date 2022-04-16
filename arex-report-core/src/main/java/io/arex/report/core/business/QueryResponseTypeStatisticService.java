package io.arex.report.core.business;

import io.arex.report.core.repository.mongo.ReplayCompareResultRepositoryImpl;
import io.arex.report.model.api.contracts.QueryCategoryStatisticRequestType;
import io.arex.report.model.api.contracts.QueryCategoryStatisticResponseType;
import io.arex.report.model.api.contracts.common.CategoryStatistic;
import io.arex.report.model.dto.CompareResultDto;
import io.arex.report.model.enums.DiffResultCode;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.internal.util.StringHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class QueryResponseTypeStatisticService {
    @Resource
    private ReplayCompareResultRepositoryImpl replayCompareResultRepository;

    public QueryCategoryStatisticResponseType categoryStatistic(QueryCategoryStatisticRequestType request) {
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
        Map<String, List<CompareResultDto>> categoryMap = compareResultDtoList.stream()
                .filter(item -> item.getCategoryName() != null)
                .collect(Collectors.groupingBy(CompareResultDto::getCategoryName));
        categoryMap.forEach((categoryName, categoryMapValue) -> {
            if (CollectionUtils.isEmpty(categoryMapValue)) {
                return;
            }
            Map<String, List<CompareResultDto>> operationNameMap = categoryMapValue.stream()
                    .filter(item -> item.getOperationName() != null)
                    .collect(Collectors.groupingBy(CompareResultDto::getOperationName));
            operationNameMap.forEach((operationName, operationNameMapValue) -> {
                if (CollectionUtils.isEmpty(operationNameMapValue)) {
                    return;
                }
                Map<Integer, List<CompareResultDto>> resultTypeMap = operationNameMapValue.stream()
                        .filter(item -> item.getDiffResultCode() != null
                                && !StringHelper.isNullOrEmptyString(item.getRecordId()))
                        .collect(Collectors.groupingBy(CompareResultDto::getDiffResultCode));
                if (resultTypeMap == null) {
                    return;
                }
                CategoryStatistic temp = new CategoryStatistic();
                temp.setCategoryName(categoryName);
                temp.setOperationName(operationName);
                temp.setTotalCaseCount(getCaseCount(operationNameMapValue));
                temp.setSuccessCaseCount(getCaseCount(resultTypeMap.get(DiffResultCode.COMPARED_WITHOUT_DIFFERENCE)));
                temp.setFailCaseCount(getCaseCount(resultTypeMap.get(DiffResultCode.COMPARED_WITH_DIFFERENCE)));
                temp.setErrorCaseCount(getCaseCount(resultTypeMap.get(DiffResultCode.COMPARED_INTERNAL_EXCEPTION))
                        + getCaseCount(resultTypeMap.get(DiffResultCode.SEND_FAILED_NOT_COMPARE)));
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
