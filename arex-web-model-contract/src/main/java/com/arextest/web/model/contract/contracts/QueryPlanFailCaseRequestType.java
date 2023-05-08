package com.arextest.web.model.contract.contracts;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/5/7.
 */
@Data
public class QueryPlanFailCaseRequestType {
    private String planId;
    private List<String> planItemIdList;
    private List<String> recordIdList;
    private List<Integer> diffResultCodeList;
}
