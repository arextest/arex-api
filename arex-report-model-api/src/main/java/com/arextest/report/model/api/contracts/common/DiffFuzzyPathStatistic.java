package com.arextest.report.model.api.contracts.common;

import lombok.Data;

import java.util.List;


@Data
public class DiffFuzzyPathStatistic {
    
    private String fuzzyPath;
    
    private Integer caseCount;
    
    private Integer sceneCount;
    
    private Integer groupTypeId;
    
    private List<String> sceneIdList;
}
