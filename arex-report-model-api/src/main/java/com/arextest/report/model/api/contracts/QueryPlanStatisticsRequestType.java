package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.PagingRequest;
import lombok.Data;


@Data
public class QueryPlanStatisticsRequestType implements PagingRequest {
    private Integer pageIndex;
    private Integer pageSize;
    private Boolean needTotal;

    
    private String appId;
    
    private Long planId;
}
