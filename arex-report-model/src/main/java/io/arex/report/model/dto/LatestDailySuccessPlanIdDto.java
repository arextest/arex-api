package io.arex.report.model.dto;

import lombok.Data;


@Data
public class LatestDailySuccessPlanIdDto {
    
    private String dateTime;
    
    private String appId;
    
    private Long planId;
    
    private Long dataChangeCreateTime;
}
