package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
public class ReplayCompareConfig {

    /**
     * comparison configuration
     */
    List<ReplayComparisonItem> replayComparisonItems;

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class ReplayComparisonItem extends ComparisonSummaryConfiguration{
        private String operationId;
        private List<DependencyComparisonItem> dependencyComparisonItems;
    }
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DependencyComparisonItem extends ComparisonSummaryConfiguration{
        private String operationId;
        private String dependencyId;
        private String dependencyType;
        private String dependencyName;

    }
}