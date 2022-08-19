package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryPlanItemStatisticsRequestType {
    
    private String planId;
    
    private String planItemId;
}
