package com.arextest.web.model.contract.contracts.config.replay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
public class ReplayCompareConfig {

    GlobalComparisonItem globalComparisonItem;
    /**
     * comparison configuration
     */
    List<ReplayComparisonItem> replayComparisonItems;

    public static class GlobalComparisonItem extends ComparisonSummaryConfiguration {

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class ReplayComparisonItem extends ComparisonSummaryConfiguration {
        private String operationId;
        private List<DependencyComparisonItem> dependencyComparisonItems;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DependencyComparisonItem extends ComparisonSummaryConfiguration {
        @JsonIgnore
        private String dependencyId;
        private String dependencyType;
        private String dependencyName;

    }
}