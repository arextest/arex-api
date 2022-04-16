package io.arex.report.model.api.contracts;

import lombok.Data;


@Data
public class QueryScenesRequestType {
    private Long planItemId;
    private String categoryName;
    private String operationName;
    private String differenceName;
}
