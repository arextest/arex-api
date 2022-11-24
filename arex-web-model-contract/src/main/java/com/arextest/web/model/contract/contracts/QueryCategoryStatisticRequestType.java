package com.arextest.web.model.contract.contracts;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class QueryCategoryStatisticRequestType {
    @NotBlank(message = "PlanItemId cannot be empty")
    private String planItemId;
}
