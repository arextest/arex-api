package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSAddItemRequestType {
    private String id;
    private String workspaceName;
    private String userName;
    private String[] parentPath;
    private String nodeName;
    private Integer nodeType;
}
