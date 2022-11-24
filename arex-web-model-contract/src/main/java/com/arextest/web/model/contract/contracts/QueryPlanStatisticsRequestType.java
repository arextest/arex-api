package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.PagingRequest;
import lombok.Data;


@Data
public class QueryPlanStatisticsRequestType implements PagingRequest {
    private Integer pageIndex;
    private Integer pageSize;
    private Boolean needTotal;

    
    private String appId;
    
    private String planId;
}
