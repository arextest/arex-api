package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSNodeType {
    private String nodeName;
    private Integer nodeType;
    private String infoId;
    private List<FSNodeType> children;
}
