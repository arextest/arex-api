package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.contracts.common.AppCaseDailyResult;
import lombok.Data;

import java.util.List;


@Data
public class DashboardAllAppDailyResultsResponseType {

    private List<AppCaseDailyResult> caseResults;
}
