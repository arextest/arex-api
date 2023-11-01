package com.arextest.web.model.contract.contracts.manualreport;

import java.util.List;

import lombok.Data;

@Data
public class InitManualReportResponseType {
    private String reportId;
    private String reportName;
    private List<ReportInterfaceType> interfaces;
}
