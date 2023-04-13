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

    private String exceptionMsg;

    @Data
    public static class LogInfo {

        private List<NodeEntity> nodePath;
        @JsonIgnore
        private int unmatchedType;
        private int logIndex;
    }
}