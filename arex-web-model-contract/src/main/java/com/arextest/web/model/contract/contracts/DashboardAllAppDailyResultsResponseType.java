package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.AppCaseDailyResult;
import lombok.Data;

import java.util.List;


@Data
public class DashboardAllAppDailyResultsResponseType {

    private List<AppCaseDailyResult> caseResults;
}
