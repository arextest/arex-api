package io.arex.report.model.api.contracts;

import io.arex.report.model.api.contracts.common.DiffFuzzyPathStatistic;
import lombok.Data;

import java.util.List;


@Data
public class QueryDiffAggInfoResponseType {
    private List<DiffFuzzyPathStatistic> diffPathStaList;
}
