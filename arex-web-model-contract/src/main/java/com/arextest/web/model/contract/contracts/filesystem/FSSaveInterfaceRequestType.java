package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.web.model.contract.contracts.common.KeyValuePairType;
import com.arextest.web.model.contract.contracts.common.ScriptBlockType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FSSaveInterfaceRequestType {

  @NotBlank(message = "Interface id cannot be empty")
  private String id;
  private String workspaceId;
  private AddressType address;
  private List<ScriptBlockType> preRequestScripts;
  private List<ScriptBlockType> testScripts;
  private BodyType body;
  private List<KeyValuePairType> headers;
  private List<KeyValuePairType> params;
  private AuthType auth;
  private AddressType testAddress;
  private String description;
  private Map<String, Object> customTags;
  private String operationId;
  private String operationResponse;
  private Set<String> labelIds;
}
