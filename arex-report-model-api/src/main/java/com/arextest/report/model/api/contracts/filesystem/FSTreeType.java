package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.Map;

@Data
public class FSTreeType {
    private String id;
    private String workspaceName;
    private String userName;
    private Map<String, FSNodeType> roots;
}
