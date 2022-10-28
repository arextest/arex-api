package com.arextest.report.model.api.contracts.filesystem;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import lombok.Data;

import java.util.List;

@Data
public class FSSaveInterfaceRequestType {
    private String id;
    private String workspaceId;
    private AddressType address;
    private String preRequestScript;
    private String testScript;
    private BodyType body;
    private List<KeyValuePairType> headers;
    private List<KeyValuePairType> params;
    private AuthType auth;
    private AddressType testAddress;
}
