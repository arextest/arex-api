package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSTreeType {
    private String id;
    private String workspaceName;
    private String userName;
    private List<FSNodeType> roots;
}
