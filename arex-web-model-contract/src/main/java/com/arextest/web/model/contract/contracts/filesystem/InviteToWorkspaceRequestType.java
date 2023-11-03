package com.arextest.web.model.contract.contracts.filesystem;

import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InviteToWorkspaceRequestType {

  @NotBlank(message = "arexUiUrl cannot be empty")
  private String arexUiUrl;
  private String invitor;
  @NotNull(message = "UserNames cannot be empty")
  private Set<String> userNames;
  @NotBlank(message = "Workspace Id cannot be empty")
  private String workspaceId;
  private Integer role;
}
