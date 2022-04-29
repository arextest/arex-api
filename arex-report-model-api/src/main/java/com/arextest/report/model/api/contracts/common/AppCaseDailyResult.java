package com.arextest.report.model.api.contracts.common;

import lombok.Data;

import java.util.List;


@Data
public class AppCaseDailyResult {
    private String date;
    private List<AppCaseResult> caseResults;
}
