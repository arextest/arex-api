package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.CategoryStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryCategoryStatisticResponseType {
    private List<CategoryStatistic> categoryStatisticList;
}
