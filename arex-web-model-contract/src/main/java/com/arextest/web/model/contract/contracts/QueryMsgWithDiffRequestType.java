package com.arextest.web.model.contract.contracts;

import lombok.Data;

@Data
public class QueryMsgWithDiffRequestType {

    private String compareResultId;

    private String logIndexes;
}
