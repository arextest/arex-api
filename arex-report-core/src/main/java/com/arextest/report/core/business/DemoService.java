package com.arextest.report.core.business;

import com.arextest.report.core.repository.ReplayCompareResultRepository;
import com.arextest.report.model.api.contracts.QueryCompareResultsByPageRequestType;
import com.arextest.report.model.api.contracts.QueryCompareResultsByPageResponseType;
import com.arextest.report.model.api.contracts.common.CompareResult;
import com.arextest.report.model.dto.CompareResultDto;
import com.arextest.report.model.mapper.CompareResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class DemoService {
    @Resource
    private ReplayCompareResultRepository repository;

    public QueryCompareResultsByPageResponseType queryCompareResultsByPage(QueryCompareResultsByPageRequestType request) {
        QueryCompareResultsByPageResponseType response = new QueryCompareResultsByPageResponseType();
        Pair<List<CompareResultDto>, Long> result =
                repository.queryCompareResultByPage(request.getPlanId(), request.getPageSize(), request.getPageIndex());
        List<CompareResult> contracts =
                result.getLeft().stream().map(CompareResultMapper.INSTANCE::contractFromDto).collect(
                        Collectors.toList());
        response.setCompareResults(contracts);
        response.setTotalCount(result.getRight());
        return response;
    }
}
