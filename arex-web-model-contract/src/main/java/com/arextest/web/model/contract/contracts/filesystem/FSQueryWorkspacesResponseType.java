package com.arextest.web.model.contract.contracts.filesystem;


import lombok.Data;

import java.util.List;

@Data
public class FSQueryWorkspacesResponseType {
    private List<WorkspaceType> workspaces;
}
