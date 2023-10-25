package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.DiffFuzzyPathStatistic;

import lombok.Data;

@Data
public class QueryDiffAggInfoResponseType {
    private List<DiffFuzzyPathStatistic> diffPathStaList;
}
