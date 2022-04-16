package io.arex.report.model.http;

import lombok.Data;


@Data
public class ReplayCaseCountRequest {
    private String appId;
    
    private Long beginTime;
    
    private Long endTime;
    private String service;
    private String operation;
    private String subject;
    
    private Integer env;
}
