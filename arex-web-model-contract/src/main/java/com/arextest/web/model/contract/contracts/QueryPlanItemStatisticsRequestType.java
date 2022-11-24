package com.arextest.web.model.contract.contracts;

import lombok.Data;


@Data
public class QueryPlanItemStatisticsRequestType {
    
    private String planId;
    
    private String planItemId;
}
