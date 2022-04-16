package io.arex.report.model.api.contracts;

import io.arex.report.model.api.contracts.common.AppCaseDailyResult;
import lombok.Data;

import java.util.List;


@Data
public class DashboardAllAppDailyResultsResponseType {

    private List<AppCaseDailyResult> caseResults;
}
