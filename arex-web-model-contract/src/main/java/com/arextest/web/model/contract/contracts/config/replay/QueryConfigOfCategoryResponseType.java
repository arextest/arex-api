package com.arextest.web.model.contract.contracts.config.replay;

import java.util.Set;
import lombok.Data;

@Data
public class QueryConfigOfCategoryResponseType extends ComparisonSummaryConfiguration {
  private Set<String> ignoreNodeSet;
}
