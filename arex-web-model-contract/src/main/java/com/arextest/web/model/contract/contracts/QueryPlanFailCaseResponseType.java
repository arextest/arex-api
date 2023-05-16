package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/5/7.
 */
@Data
public class QueryPlanFailCaseResponseType {
    private List<FailCaseInfo> failCaseInfoList;

    @Data
    public static class FailCaseInfo {
        private String operationId;
        private List<String> replayIdList;
    }
}
