package com.arextest.web.model.contract.contracts.manualreport;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class InitManualReportRequestType {
    @NotBlank(message = "workspaceId cannot be empty")
    private String workspaceId;
    private String reportName;
    private String operator;
    private List<String> caseIds;
}
