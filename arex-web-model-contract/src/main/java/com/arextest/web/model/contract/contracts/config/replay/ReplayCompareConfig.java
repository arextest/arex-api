package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;

import java.util.List;

@Data
public class ReplayCompareConfig {

    /**
     * comparison configuration
     */
    List<ReplayComparisonItem> replayComparisonItems;

    @Data
    public static class ReplayComparisonItem extends ComparisonSummaryConfiguration{
        private String operationId;
    }
}