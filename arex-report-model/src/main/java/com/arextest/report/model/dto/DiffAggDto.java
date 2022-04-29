package com.arextest.report.model.dto;

import lombok.Data;

import java.util.Map;


@Data
public class DiffAggDto extends BaseDto {
    
    private Long planItemId;
    
    private Long planId;
    
    private Long operationId;
    
    private String categoryName;
    
    private String operationName;

    
    private Map<String, Map<String, SceneDetailDto>> differences;

    
    private Map<String, Integer> diffCaseCounts;

}
