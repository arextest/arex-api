package io.arex.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryPlanItemStatisticsRequestType {
    
    private Long planId;
    
    private Long planItemId;
}
