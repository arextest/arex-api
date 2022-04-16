package io.arex.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryMsgWithDiffRequestType {
    
    private String compareResultId;
    
    private String logIndexes;
}
