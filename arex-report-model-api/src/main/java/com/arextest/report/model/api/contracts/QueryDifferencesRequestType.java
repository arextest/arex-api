package com.arextest.report.model.api.contracts;

import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class QueryDifferencesRequestType {
    @NotBlank(message = "PlanItemId cannot be empty")
    private String planItemId;
    @NotBlank(message = "Category Name cannot be empty")
    private String categoryName;
    @NotBlank(message = "Operation Name cannot be empty")
    private String operationName;
}
