package com.arextest.web.model.contract.contracts.config.replay;

import lombok.Data;

import java.util.List;

@Data
public class ReplayConfiguration {

    /**
     * @see ScheduleConfiguration
     */
    ScheduleConfiguration scheduleConfiguration;

    /**
     * comparison configuration
     */
    List<ReplayComparisonConfig> replayComparisonConfigs;

    @Data
    public static class ReplayComparisonConfig extends ComparisonSummaryConfiguration{
        private String operationId;
    }
}