package com.arextest.report.model.api.contracts.filesystem;


import lombok.Data;

import java.util.List;

@Data
public class FSQueryWorkspacesResponseType {
    private List<WorkspaceType> workspaces;
}
