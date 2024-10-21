package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FSAddItemRequestType {

  private String id;
  private String workspaceName;
  private String userName;
  private List<String> parentPath;
  @NotBlank(message = "Node name cannot be empty")
  private String nodeName;
  @NotNull(message = "NodeType cannot be empty")
  private Integer nodeType;
  /**
   * com.arextest.web.model.enums.CaseSourceType
   */
  private int caseSourceType;
}
