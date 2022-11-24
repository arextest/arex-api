package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.web.model.contract.contracts.common.KeyValuePairType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class FSSaveInterfaceRequestType {
    @NotBlank(message = "Interface id cannot be empty")
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
