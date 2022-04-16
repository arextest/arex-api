package io.arex.report.model.api.contracts.common;

import lombok.Data;

import java.util.List;


@Data
public class CaseDetailResult {
    
    private String replayId;
    
    private String recordId;
    
    private Integer diffResultCode;
}
