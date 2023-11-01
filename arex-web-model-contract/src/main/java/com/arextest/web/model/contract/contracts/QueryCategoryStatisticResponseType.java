package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.CategoryStatistic;
import java.util.List;
import lombok.Data;

@Data
public class QueryCategoryStatisticResponseType {

  private List<CategoryStatistic> categoryStatisticList;
}
