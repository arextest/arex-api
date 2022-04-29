package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryDiffAggInfoRequestType {
    
    private Long planId;
    
    private Long operationId;
    
    private String categoryName;
    
    private String operationName;
}
