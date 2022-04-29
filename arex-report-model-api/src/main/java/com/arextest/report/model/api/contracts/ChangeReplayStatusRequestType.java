package com.arextest.report.model.api.contracts;

import lombok.Data;

import java.util.List;


@Data
public class ChangeReplayStatusRequestType {
    private Long planId;
    private Integer totalCaseCount;
    private Integer status;
    private List<ReplayItem> items;


    @Data
    public static class ReplayItem {
        private Long planItemId;
        private Integer totalCaseCount;
        private Integer status;
    }
}
