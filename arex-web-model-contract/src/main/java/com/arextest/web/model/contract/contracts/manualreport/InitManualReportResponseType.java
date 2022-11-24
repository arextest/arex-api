package com.arextest.web.model.contract.contracts.manualreport;

import lombok.Data;

import java.util.List;

@Data
public class InitManualReportResponseType {
    private String reportId;
    private String reportName;
    private List<ReportInterfaceType> interfaces;
}
