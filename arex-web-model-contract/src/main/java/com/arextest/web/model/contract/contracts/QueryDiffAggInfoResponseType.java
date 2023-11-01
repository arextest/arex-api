package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.DiffFuzzyPathStatistic;
import java.util.List;
import lombok.Data;

@Data
public class QueryDiffAggInfoResponseType {

  private List<DiffFuzzyPathStatistic> diffPathStaList;
}
