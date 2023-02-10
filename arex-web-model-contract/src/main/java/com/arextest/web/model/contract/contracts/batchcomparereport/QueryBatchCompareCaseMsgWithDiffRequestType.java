package com.arextest.web.model.contract.contracts.batchcomparereport;

import lombok.Data;

/**
 * Created by rchen9 on 2023/2/9.
 */
@Data
public class QueryBatchCompareCaseMsgWithDiffRequestType {
    private String caseId;
    private int index;
    private int logIndex;
}
