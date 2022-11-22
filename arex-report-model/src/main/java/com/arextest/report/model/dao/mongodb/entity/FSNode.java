package com.arextest.report.model.dao.mongodb.entity;

import lombok.Data;

import java.util.List;

@Data
public class FSNode {
    private String nodeName;
    private Integer nodeType;
    private String infoId;
    private String method;  // available for nodeType equal 1
    private List<String> labelIds;
    private List<FSNode> children;
}
