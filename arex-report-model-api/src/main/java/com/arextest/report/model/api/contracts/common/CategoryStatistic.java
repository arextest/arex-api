package com.arextest.report.model.api.contracts.common;

import lombok.Data;


@Data
public class CategoryStatistic {
    
    private String categoryName;
    
    private String operationName;
    
    private Integer totalCaseCount;
    private Integer successCaseCount;
    private Integer failCaseCount;
    private Integer errorCaseCount;
}
