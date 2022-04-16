package io.arex.report.model.api.contracts;

import io.arex.report.model.api.contracts.common.CategoryStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryCategoryStatisticResponseType {
    private List<CategoryStatistic> categoryStatisticList;
}
