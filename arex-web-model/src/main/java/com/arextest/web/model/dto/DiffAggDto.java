package com.arextest.web.model.dto;

import java.util.Map;

import lombok.Data;

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
