package com.arextest.web.model.contract.contracts.replay;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/6/7.
 */
@Data
public class AnalyzeCompareResultsRequestType {

    private List<AnalyzeCompareResultItem> analyzeCompareResultItems;

    @Data
    public static class AnalyzeCompareResultItem {

        private String planId;

        private String planItemId;

        private String operationId;

        private String serviceName;

        private String categoryName;

        private String operationName;

        private String replayId;

        private String recordId;

        private Integer diffResultCode;

        private MsgInfo msgInfo;

    }

    @Data
    public static class MsgInfo {
        private int msgMiss;
    }
}
