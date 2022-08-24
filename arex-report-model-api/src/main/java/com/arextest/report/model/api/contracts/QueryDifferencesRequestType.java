package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryDifferencesRequestType {
    private String planItemId;
    private String categoryName;
    private String operationName;
}
