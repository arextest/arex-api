package com.arextest.web.model.contract.contracts.filesystem;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FSAddWorkspaceRequestType {

  private String workspaceName;
  @NotBlank(message = "UserName cannot be empty")
  private String userName;
}
