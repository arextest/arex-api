package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.CompareResult;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PushCompareResultsRequestType {

  @NotNull(message = "Results cannot be null")
  List<CompareResult> results;
}
