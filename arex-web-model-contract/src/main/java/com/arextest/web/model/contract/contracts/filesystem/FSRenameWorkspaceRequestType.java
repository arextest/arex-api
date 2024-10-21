package com.arextest.web.model.contract.contracts.filesystem;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FSRenameWorkspaceRequestType {

  @NotBlank(message = "WorkspaceId cannot empty")
  private String id;
  @NotBlank(message = "New workspace name cannot be empty")
  private String workspaceName;
}
