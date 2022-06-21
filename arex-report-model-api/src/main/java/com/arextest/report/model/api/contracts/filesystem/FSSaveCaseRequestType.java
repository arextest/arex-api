package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSSaveCaseRequestType {
    private String id;
    private String preRequestScript;
    private String testScript;
    private BodyType body;
    private List<KeyValuePairType> headers;
    private List<KeyValuePairType> params;
    private AuthType auth;
}
