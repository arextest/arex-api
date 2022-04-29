package com.arextest.report.model.api.contracts;

import com.arextest.report.model.api.contracts.common.LogEntity;
import lombok.Data;

import java.util.List;


@Data
public class QueryReplayMsgResponseType {
    
    private boolean baseMsgDownload;
    private boolean testMsgDownload;
    private String baseMsg;
    private String testMsg;
    private Integer diffResultCode;
    private List<LogEntity> logs;
}
