package com.arextest.web.model.contract.contracts.record;

import lombok.Data;

@Data
public class ListRecordRequestType {
    private String appId;
    private String operationName;
    private String operationType;
    private Integer pageSize;
    private String lastId;
}
