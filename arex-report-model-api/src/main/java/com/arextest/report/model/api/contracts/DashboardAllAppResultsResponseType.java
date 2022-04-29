package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.contracts.common.AppCaseResult;
import lombok.Data;

import java.util.List;


@Data
public class DashboardAllAppResultsResponseType {

    private List<AppCaseResult> caseResults;
}
