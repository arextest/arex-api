package com.arextest.web.core.repository;

import com.arextest.web.model.dto.CompareResultDto;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ReplayCompareResultRepository extends RepositoryProvider {
    boolean saveResults(List<CompareResultDto> results);

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

    List<CompareResultDto> queryCompareResults(String planId, List<String> planItemIdList,
                                                   List<String> recordIdList, List<Integer> diffResultCodeList);
}
