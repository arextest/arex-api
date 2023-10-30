package com.arextest.web.model.contract.contracts;

import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * Created by rchen9 on 2023/5/7.
 */
@Data
public class QueryPlanFailCaseResponseType {
    private List<FailCaseInfo> failCaseInfoList;

    @Data
    public static class FailCaseInfo {
        private String operationId;
        private Set<String> replayIdList;
    }
}
