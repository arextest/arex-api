package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import lombok.Data;

import java.util.List;


@Data
public class QueryReplayMsgResponseType extends DesensitizationResponseType {
    
    private boolean baseMsgDownload;
    private boolean testMsgDownload;
    private String baseMsg;
    private String testMsg;
    private Integer diffResultCode;
    private List<LogEntity> logs;
}
