package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.web.model.contract.contracts.common.KeyValuePairType;
import lombok.Data;

import java.util.List;

@Data
public class FSQueryInterfaceResponseType {
    private String id;
    private AddressType address;
    private String preRequestScript;
    private String testScript;
    private BodyType body;
    private List<KeyValuePairType> headers;
    private List<KeyValuePairType> params;
    private AuthType auth;
    private AddressType testAddress;
}
