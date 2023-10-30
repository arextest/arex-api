package com.arextest.web.model.contract.contracts.config.replay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class ReplayCompareConfig {

  /**
   * comparison configuration
   */
  List<ReplayComparisonItem> replayComparisonItems;

  private Boolean skipAssemble = Boolean.TRUE;

  @Data
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class ReplayComparisonItem extends ComparisonSummaryConfiguration {

    private String operationId;
    private DependencyComparisonItem defaultDependencyComparisonItem;
    private List<DependencyComparisonItem> dependencyComparisonItems;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  @ToString(callSuper = true)
  public static class DependencyComparisonItem extends ComparisonSummaryConfiguration {

    @JsonIgnore
    private String dependencyId;

  }
}