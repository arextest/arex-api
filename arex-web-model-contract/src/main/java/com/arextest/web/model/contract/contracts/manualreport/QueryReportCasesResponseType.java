package com.arextest.web.model.contract.contracts.manualreport;

import java.util.List;

import lombok.Data;

@Data
public class QueryReportCasesResponseType {
    List<ReportCaseType> reportCases;
}
