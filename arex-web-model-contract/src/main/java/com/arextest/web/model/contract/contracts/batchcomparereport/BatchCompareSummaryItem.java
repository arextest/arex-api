package com.arextest.web.model.contract.contracts.batchcomparereport;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import lombok.Data;

@Data
public class BatchCompareSummaryItem {

    private int unMatchedType;
    private String fuzzyPath;
    private int errorCount;

    private LogEntity logEntity;
    private String logId;
}