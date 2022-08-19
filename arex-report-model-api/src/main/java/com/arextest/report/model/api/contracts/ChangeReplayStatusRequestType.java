package com.arextest.report.model.api.contracts;

import lombok.Data;

import java.util.List;


@Data
public class ChangeReplayStatusRequestType {
    private String planId;
    private Integer totalCaseCount;
    private Integer status;
    private List<ReplayItem> items;


    @Data
    public static class ReplayItem {
        private String planItemId;
        private Integer totalCaseCount;
        private Integer status;
    }
}
