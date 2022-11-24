package com.arextest.web.model.contract.contracts;

import lombok.Data;


@Data
public class DashboardAllAppDailyResultsRequestType {

    
    private String appId;
    
    private Long startTime;
    
    private Long endTime;

}
