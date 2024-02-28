package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class BatchGetInterfaceCaseRequestType {

    private String workspaceId;

    private List<FSNodeType> nodes;
}
