package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSQueryInterfaceResponseType {
    private String id;
    private String endpoint;
    private String method;
    private String preRequestScript;
    private String testScript;
    private BodyType body;
    private List<KeyValuePairType> headers;
    private List<KeyValuePairType> params;
    private AuthType auth;
}
