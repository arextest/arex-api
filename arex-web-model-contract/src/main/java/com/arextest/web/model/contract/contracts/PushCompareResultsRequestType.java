package com.arextest.web.model.contract.contracts;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.arextest.web.model.contract.contracts.common.CompareResult;

import lombok.Data;

@Data
public class PushCompareResultsRequestType {
    @NotNull(message = "Results cannot be null")
    List<CompareResult> results;
}
