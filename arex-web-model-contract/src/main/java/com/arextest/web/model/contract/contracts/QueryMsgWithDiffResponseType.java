package com.arextest.web.model.contract.contracts;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.LogEntity;

import lombok.Data;

@Data
public class QueryMsgWithDiffResponseType extends DesensitizationResponseType {
    private String replayId;
    private String recordId;
    private int diffResultCode;
    private String baseMsg;
    private String testMsg;
    private List<LogEntity> logs;
}
