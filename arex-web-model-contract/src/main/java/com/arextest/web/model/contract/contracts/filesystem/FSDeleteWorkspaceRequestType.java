package com.arextest.web.model.contract.contracts.filesystem;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FSDeleteWorkspaceRequestType {

  private String userName;
  @NotBlank(message = "Workspace id cannot be empty")
  private String workspaceId;
}
