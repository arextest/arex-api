package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryDiffAggInfoRequestType {
    
    private String planId;
    
    private String operationId;
    
    private String categoryName;
    
    private String operationName;
}
