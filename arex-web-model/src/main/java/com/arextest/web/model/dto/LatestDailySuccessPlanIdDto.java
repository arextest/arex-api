package com.arextest.web.model.dto;

import lombok.Data;

@Data
public class LatestDailySuccessPlanIdDto {

    private String dateTime;

    private String appId;

    private String planId;

    private Long dataChangeCreateTime;
}
