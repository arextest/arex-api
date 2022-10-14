package com.arextest.report.model.api.contracts.filesystem;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import lombok.Data;

import java.util.List;

@Data
public class FSQueryCaseResponseType {
    private String id;
    private AddressType address;
    private String preRequestScript;
    private String testScript;
    private BodyType body;
    private List<KeyValuePairType> headers;
    private List<KeyValuePairType> params;
    private AuthType auth;
    private AddressType baseAddress;
    private AddressType testAddress;
    private String recordId;
}
