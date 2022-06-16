package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.Map;

@Data
public class FSNodeType {
    private String nodeName;
    private Integer nodeType;
    private String infoId;
    private Map<String, FSNodeType> children;
}
