package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import lombok.Data;

@Data
public class FSQueryWorkspacesResponseType {

  private List<WorkspaceType> workspaces;
}
