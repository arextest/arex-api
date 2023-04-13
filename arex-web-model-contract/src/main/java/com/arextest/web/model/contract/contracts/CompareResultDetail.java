package com.arextest.web.model.contract.contracts;

import com.arextest.web.model.contract.contracts.common.NodeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class CompareResultDetail {
    private String id;
    private String categoryName;
    private String operationName;
    private int diffResultCode;

    private List<LogInfo> logInfos;
    private String baseMsg;
    private String testMsg;

    @Data
    public static class LogInfo {

        private String logInfo;
        private List<NodeEntity> nodePath;
        @JsonIgnore
        private int unmatchedType;
        private int logIndex;
    }
}