package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryMsgSchemaRequestType {
    
    private String id;
    
    private String msg;
    
    private String listPath;
    private boolean useTestMsg;
}
