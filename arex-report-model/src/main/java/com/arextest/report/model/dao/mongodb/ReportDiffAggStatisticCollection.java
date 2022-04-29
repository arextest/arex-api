package com.arextest.report.model.dao.mongodb;

import com.arextest.report.model.dao.mongodb.entity.SceneDetail;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Data
@Document(collection = "ReportDiffAggStatistic")
public class ReportDiffAggStatisticCollection {
    
    private Long planItemId;
    
    private Long planId;
    
    private Long operationId;
    
    private String categoryName;
    
    private String operationName;

    
    private Map<String,Map<String, SceneDetail>> differences;

    
    private Map<String, Integer> diffCaseCounts;
}
