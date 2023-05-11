package com.arextest.web.model.contract.contracts.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
public class RecordItem {
    private String id;
    private String categoryType;
    private String replayId;
    private String recordId;
    private String appId;
    private int recordEnvironment;
    /**
     * millis from utc format without timezone
     */
    private long creationTime;
    private Target targetRequest;
    private Target targetResponse;
    /**
     * the value required and empty allowed
     * for example: pattern of servlet web api
     */
    private String operationName;
    /**
     * record the version of recorded data
     */
    private String recordVersion;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Target {
        private String body;
        private Map<String, Object> attributes;
        private String type;
    }
}
