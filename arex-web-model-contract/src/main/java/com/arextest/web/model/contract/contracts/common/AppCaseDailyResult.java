package com.arextest.web.model.contract.contracts.common;

import lombok.Data;

import java.util.List;


@Data
public class AppCaseDailyResult {
    private String date;
    private List<AppCaseResult> caseResults;
}
