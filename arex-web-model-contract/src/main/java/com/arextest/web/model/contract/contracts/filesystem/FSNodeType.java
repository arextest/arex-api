package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class FSNodeType {
    private String nodeName;
    private Integer nodeType;
    private String infoId;
    private String method;
    /**
     * availbale for nodeType equal 2
     *
     * @see com.arextest.web.model.enums.CaseSourceType
     */
    private int caseSourceType;
    private Set<String> labelIds;
    private List<FSNodeType> children;
}
