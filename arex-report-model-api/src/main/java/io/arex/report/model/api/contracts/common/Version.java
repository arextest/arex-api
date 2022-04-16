package io.arex.report.model.api.contracts.common;

import lombok.Data;


@Data
public class Version {
    
    private String coreVersion;
    
    private String extVersion;
    
    private String caseRecordVersion;
}
