package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.AppCaseDailyResult;

import lombok.Data;

@Data
public class DashboardAllAppDailyResultsResponseType {

    private List<AppCaseDailyResult> caseResults;
}
