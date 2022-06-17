package com.arextest.report.model.dto.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSInterfaceDto {
    private String id;
    private String name;
    private String endpoint;
    private String method;
    private String preRequestScript;
    private String testScript;
    private BodyDto body;
    private List<KeyValuePairDto> headers;
    private List<KeyValuePairDto> params;
    private AuthDto auth;
}
