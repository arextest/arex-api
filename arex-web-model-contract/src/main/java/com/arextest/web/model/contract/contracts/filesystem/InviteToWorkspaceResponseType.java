package com.arextest.web.model.contract.contracts.filesystem;

import java.util.Set;
import lombok.Data;

@Data
public class InviteToWorkspaceResponseType {

  private Set<String> successUsers;
  private Set<String> failedUsers;
}
