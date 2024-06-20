package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
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
}
