package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/5/7.
 */
@Data
public class QueryPlanFailCaseRequestType {
    private String planId;
    private List<String> planItemId;
    private List<String> recordId;
    private List<Integer> diffResultCodeList;
}
