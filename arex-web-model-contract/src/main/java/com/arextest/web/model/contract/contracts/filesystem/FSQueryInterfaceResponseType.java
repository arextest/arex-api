package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.web.model.contract.contracts.common.KeyValuePairType;
import com.arextest.web.model.contract.contracts.common.ScriptBlockType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FSQueryInterfaceResponseType extends FSQueryItemType {
    private AddressType address;
    private List<ScriptBlockType> preRequestScripts;
    private List<ScriptBlockType> testScripts;
    private BodyType body;
    private List<KeyValuePairType> headers;
    private List<KeyValuePairType> params;
    private AuthType auth;
    private AddressType testAddress;
    private String description;
    private String operationId;
    private String operationResponse;
}
