package com.arextest.web.model.contract.contracts;

import lombok.Data;

@Data
public class FullLinkInfoItem {
    private String id;
    /**
     * -1 : exception
     * 0: success
     * 1: value diff
     * 2: left call missing
     * 4: right call missing
     */
    private int code;
    private String categoryName;
    private String operationName;
    private String instanceId;

    /**
     * the operationName can be positioned in the system
     */
    private boolean foundInSystem;
    private String operationId;
    private String dependencyId;
}