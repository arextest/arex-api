package com.arextest.web.model.contract.contracts.manualreport;

import lombok.Data;

import java.util.List;

@Data
public class QueryReportCasesResponseType {
    List<ReportCaseType> reportCases;
}
