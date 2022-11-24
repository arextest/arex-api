package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.DiffFuzzyPathStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryDiffAggInfoResponseType {
    private List<DiffFuzzyPathStatistic> diffPathStaList;
}
