package com.arextest.web.model.dao.mongodb.entity;

import lombok.Data;

import java.util.List;

@Data
public class FSNode {
    private String nodeName;
    private Integer nodeType;
    private String infoId;
    private String method;  // available for nodeType equal 1
    /**
     * availbale for nodeType equal 2
     * @see com.arextest.web.model.enums.CaseSourceType
     */
    private int caseType;
    private List<String> labelIds;
    private List<FSNode> children;
}
