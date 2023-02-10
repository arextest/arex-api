package com.arextest.web.model.dao.mongodb.batchcomparereport;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.dao.mongodb.ModelBase;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by rchen9 on 2023/2/8.
 */
@Data
@Document(collection = "BatchCompareReportStatistics")
public class BatchCompareReportStatisticsCollection extends ModelBase {

    private String planId;
    private String interfaceId;
    private int unMatchedType;
    private String fuzzyPath;
    private int errorCount;

    // 卡片示例
    private String logId;
    private LogEntity logEntity;
}
