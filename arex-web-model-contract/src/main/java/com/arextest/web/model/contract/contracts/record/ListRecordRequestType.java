package com.arextest.web.model.contract.contracts.record;

import lombok.Data;

import java.util.List;

@Data
public class ListRecordRequestType {
    private String appId;
    private String operationName;
    private List<String> operationTypes;
    private Integer pageSize;
    private Integer pageIndex;
    private Long beginTime;
    private Long endTime;
}