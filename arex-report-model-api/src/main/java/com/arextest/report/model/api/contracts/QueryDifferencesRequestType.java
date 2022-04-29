package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryDifferencesRequestType {
    private Long planItemId;
    private String categoryName;
    private String operationName;
}
