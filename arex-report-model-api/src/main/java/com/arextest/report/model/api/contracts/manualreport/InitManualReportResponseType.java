package com.arextest.report.model.api.contracts.manualreport;

import lombok.Data;

import java.util.List;

@Data
public class InitManualReportResponseType {
    private String reportId;
    private String reportName;
    private List<ReportInterfaceType> interfaces;
}
