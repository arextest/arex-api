package com.arextest.web.model.contract.contracts.batchcomparereport;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/8.
 */
@Data
public class QueryBatchCompareSummaryResponseType {

    private List<BatchCompareSummaryItem> batchCompareSummaryItems;
}
