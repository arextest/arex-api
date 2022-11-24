package com.arextest.web.model.contract.contracts.common;

import lombok.Data;


@Data
public class CaseDetailResult {
    
    private String replayId;
    
    private String recordId;
    
    private Integer diffResultCode;
}
