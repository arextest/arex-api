package com.arextest.report.model.api.contracts.manualreport;

import lombok.Data;

import java.util.List;

@Data
public class QueryReportCasesRequestType {
    List<String> reportCaseIds;
}
