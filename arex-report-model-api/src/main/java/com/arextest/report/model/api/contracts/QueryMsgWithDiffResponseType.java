package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.contracts.common.LogEntity;
import lombok.Data;

import java.util.List;


@Data
public class QueryMsgWithDiffResponseType {
    private String replayId;
    private String recordId;
    private int diffResultCode;
    private String baseMsg;
    private String testMsg;
    private List<LogEntity> logs;
}
