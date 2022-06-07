package com.arextest.report.model.dto;

import lombok.Data;

@Data
public class ServletMockerDto {
    private String id;
    private String appId;
    private String path;
    private String request;
    private String response;
}
