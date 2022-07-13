package com.arextest.report.model.api.contracts.manualreport;

import lombok.Data;

import java.util.List;

@Data
public class InitManualReportRequestType {
    private String workspaceId;
    private String reportName;
    private String operator;
    private List<String> caseIds;
}
