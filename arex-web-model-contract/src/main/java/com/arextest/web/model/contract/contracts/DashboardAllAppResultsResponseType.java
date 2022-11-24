package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.AppCaseResult;
import lombok.Data;

import java.util.List;


@Data
public class DashboardAllAppResultsResponseType {

    private List<AppCaseResult> caseResults;
}
