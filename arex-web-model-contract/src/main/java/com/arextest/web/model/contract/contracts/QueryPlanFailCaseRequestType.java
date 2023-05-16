package com.arextest.web.model.contract.contracts;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Created by rchen9 on 2023/5/7.
 */
@Data
public class QueryPlanFailCaseRequestType {
    @NotBlank(message = "planId cannot be empty")
    private String planId;
    private List<String> planItemIdList;
    private List<String> recordIdList;
    private List<Integer> diffResultCodeList;
}
