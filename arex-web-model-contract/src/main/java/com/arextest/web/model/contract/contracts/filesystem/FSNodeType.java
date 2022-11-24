package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class FSNodeType {
    private String nodeName;
    private Integer nodeType;
    private String infoId;
    private String method;
    private Set<String> labelIds;
    private List<FSNodeType> children;
}
