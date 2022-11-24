package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FSAddItemRequestType {
    private String id;
    private String workspaceName;
    private String userName;
    private String[] parentPath;
    @NotBlank(message = "Node name cannot be empty")
    private String nodeName;
    @NotNull(message = "NodeType cannot be empty")
    private Integer nodeType;
}
