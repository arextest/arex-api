package io.arex.report.model.api.contracts;

import io.arex.report.model.api.contracts.common.AppCaseResult;
import lombok.Data;

import java.util.List;


@Data
public class DashboardAllAppResultsResponseType {

    private List<AppCaseResult> caseResults;
}
