package com.arextest.web.core.business;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.model.contract.contracts.QueryCompareResultsByPageRequestType;
import com.arextest.web.model.contract.contracts.QueryCompareResultsByPageResponseType;
import com.arextest.web.model.contract.contracts.common.CompareResult;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.mapper.CompareResultMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DemoService {
    @Resource
    private ReplayCompareResultRepository repository;

    public QueryCompareResultsByPageResponseType
        queryCompareResultsByPage(QueryCompareResultsByPageRequestType request) {
        QueryCompareResultsByPageResponseType response = new QueryCompareResultsByPageResponseType();
        Pair<List<CompareResultDto>, Long> result =
            repository.queryCompareResultByPage(request.getPlanId(), request.getPageSize(), request.getPageIndex());
        List<CompareResult> contracts =
            result.getLeft().stream().map(CompareResultMapper.INSTANCE::contractFromDto).collect(Collectors.toList());
        response.setCompareResults(contracts);
        response.setTotalCount(result.getRight());
        return response;
    }
}
