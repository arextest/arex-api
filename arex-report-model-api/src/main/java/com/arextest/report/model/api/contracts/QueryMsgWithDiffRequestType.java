package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryMsgWithDiffRequestType {
    
    private String compareResultId;
    
    private String logIndexes;
}
