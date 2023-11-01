package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.AppCaseResult;
import java.util.List;
import lombok.Data;

@Data
public class DashboardAllAppResultsResponseType {

  private List<AppCaseResult> caseResults;
}
