package com.arextest.web.model.contract.contracts.filesystem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FSAddItemRequestType {

  private String id;
  private String workspaceName;
  private String userName;
  private String[] parentPath;
  @NotBlank(message = "Node name cannot be empty")
  private String nodeName;
  @NotNull(message = "NodeType cannot be empty")
  private Integer nodeType;
  /**
   * @see com.arextest.web.model.enums.CaseSourceType
   */
  private int caseSourceType;
}
