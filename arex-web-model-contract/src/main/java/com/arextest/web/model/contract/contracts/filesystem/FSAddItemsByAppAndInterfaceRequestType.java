package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.web.model.contract.contracts.common.KeyValuePairType;
import com.arextest.web.model.contract.contracts.common.ScriptBlockType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author b_yu
 * @since 2024/2/6
 */
@Data
public class FSAddItemsByAppAndInterfaceRequestType {
  @NotBlank(message = "WorkspaceId cannot be empty")
  private String workspaceId;
  private List<String> parentPath;
  private String appName;
  private String interfaceName;
  @NotBlank(message = "operationId cannot be empty")
  private String operationId;
  private String nodeName;

  // for Case & Interface
  private AddressType address;
  private List<ScriptBlockType> preRequestScripts;
  private List<ScriptBlockType> testScripts;
  private BodyType body;
  private List<KeyValuePairType> headers;
  private List<KeyValuePairType> params;
  private AuthType auth;
  private AddressType testAddress;
  private Set<String> labelIds;
  private String description;

}
