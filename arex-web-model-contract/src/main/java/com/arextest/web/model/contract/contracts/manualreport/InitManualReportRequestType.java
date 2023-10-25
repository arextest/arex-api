package com.arextest.web.model.contract.contracts.manualreport;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class InitManualReportRequestType {
    @NotBlank(message = "workspaceId cannot be empty")
    private String workspaceId;
    private String reportName;
    private String operator;
    private List<String> caseIds;
}
