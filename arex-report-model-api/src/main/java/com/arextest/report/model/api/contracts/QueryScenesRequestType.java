package com.arextest.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryScenesRequestType {
    private String planItemId;
    private String categoryName;
    private String operationName;
    private String differenceName;
}
