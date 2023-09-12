package com.arextest.web.core.repository;

import com.arextest.web.model.dto.CompareResultDto;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReplayCompareResultRepository extends RepositoryProvider {
    boolean saveResults(List<CompareResultDto> results);

    boolean updateResults(List<CompareResultDto> results);

    List<CompareResultDto> findResultWithoutMsg(String planItemId);

    List<CompareResultDto> findResultWithoutMsg(String planItemId, String keyWord);

    // Pair<List<CompareResultDto>, Long> pageQueryWithoutMsg(Long planId, Long planItemId, String categoryName,
    // Integer resultType, String keyWord,
    // Integer pageIndex, Integer pageSize, Boolean needTotal);

    Pair<List<CompareResultDto>, Long> queryCompareResultByPage(String planId, Integer pageSize, Integer pageIndex);

    Pair<List<CompareResultDto>, Long> queryAllDiffMsgByPage(String planItemId, String recordId,
                                                             List<Integer> diffResultCodeList,
                                                             Integer pageSize, Integer pageIndex, Boolean needTotal);

    CompareResultDto queryCompareResultsById(String objectId);

    List<CompareResultDto> queryCompareResultsByRecordId(String planItemId, String recordId);

    boolean deleteCompareResultsByPlanId(String planId);

    int queryCompareResultCountByPlanId(String planId);

    List<CompareResultDto> queryCompareResults(String planId,
                                               List<String> planItemIdList,
                                               List<String> recordIdList,
                                               List<Integer> diffResultCodeList,
                                               List<String> showFields);

    List<CompareResultDto> queryCompareResults(List<String> planItemIdList,
                                               List<String> recordIdList);


    List<CompareResultDto> queryLatestEntryPointCompareResult(String operationId, Set<String> operationTypes, int limit);

    Map<String, List<CompareResultDto>> queryLatestCompareResultMap(String operationId, List<String> operationNames,
                                                                    List<String> operationTypes);
}
