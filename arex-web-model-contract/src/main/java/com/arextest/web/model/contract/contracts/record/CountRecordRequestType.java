package com.arextest.web.model.contract.contracts.record;

import lombok.Data;

@Data
public class CountRecordRequestType {
    private String appId;
    private String operationType;
    private String operationName;
    /**
     * milliseconds
     */
    private Long beginTime;
    private Long endTime;

}
