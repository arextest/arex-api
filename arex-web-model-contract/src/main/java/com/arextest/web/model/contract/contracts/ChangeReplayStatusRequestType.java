package com.arextest.web.model.contract.contracts;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
public class ChangeReplayStatusRequestType {
    @NotBlank(message = "Plan id cannot be empty")
    private String planId;
    private Integer totalCaseCount;
    private Integer status;
    private String errorMessage;
    private List<ReplayItem> items;


    @Data
    public static class ReplayItem {
        private String planItemId;
        private Integer totalCaseCount;
        private Integer status;
        private String errorMessage;
    }
}
