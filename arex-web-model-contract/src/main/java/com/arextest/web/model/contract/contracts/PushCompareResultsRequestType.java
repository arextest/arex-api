package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.CompareResult;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class PushCompareResultsRequestType {
    @NotNull(message = "Results cannot be null")
    List<CompareResult> results;
}
