package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.CategoryStatistic;

import lombok.Data;

@Data
public class QueryCategoryStatisticResponseType {
    private List<CategoryStatistic> categoryStatisticList;
}
