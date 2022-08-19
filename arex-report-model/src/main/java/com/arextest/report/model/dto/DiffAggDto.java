package com.arextest.report.model.dto;

import lombok.Data;

import java.util.Map;


@Data
public class DiffAggDto extends BaseDto {
    
    private String planItemId;
    
    private String planId;
    
    private String operationId;
    
    private String categoryName;
    
    private String operationName;

    
    private Map<String, Map<String, SceneDetailDto>> differences;

    
    private Map<String, Integer> diffCaseCounts;

}
