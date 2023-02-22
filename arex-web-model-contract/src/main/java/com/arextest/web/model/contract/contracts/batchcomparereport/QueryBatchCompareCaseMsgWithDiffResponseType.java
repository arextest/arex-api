package com.arextest.web.model.contract.contracts.batchcomparereport;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import lombok.Data;

/**
 * Created by rchen9 on 2023/2/14.
 */
@Data
public class QueryBatchCompareCaseMsgWithDiffResponseType {
    private String baseMsg;
    private String testMsg;
    private LogEntity logEntity;
}
