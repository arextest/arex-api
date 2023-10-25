package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.AppCaseResult;

import lombok.Data;

@Data
public class DashboardAllAppResultsResponseType {

    private List<AppCaseResult> caseResults;
}
