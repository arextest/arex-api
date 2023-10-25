package com.arextest.web.model.contract.contracts.common;

import java.util.List;

import lombok.Data;

@Data
public class AppCaseDailyResult {
    private String date;
    private List<AppCaseResult> caseResults;
}
