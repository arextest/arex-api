package com.arextest.web.model.dao.mongodb;

import com.arextest.web.model.dao.mongodb.entity.SceneDetail;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Data
@Document(collection = "ReportDiffAggStatistic")
public class ReportDiffAggStatisticCollection {
    
    private String planItemId;
    
    private String planId;
    
    private String operationId;
    
    private String categoryName;
    
    private String operationName;

    
    private Map<String,Map<String, SceneDetail>> differences;

    
    private Map<String, Integer> diffCaseCounts;
}
