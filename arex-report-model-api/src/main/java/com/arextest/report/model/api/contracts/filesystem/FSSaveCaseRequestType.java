package com.arextest.report.model.api.contracts.filesystem;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Data
public class FSSaveCaseRequestType {
    @NotBlank(message = "Case id cannot be empty")
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
    private Set<String> labelIds;
}
