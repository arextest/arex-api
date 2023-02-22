package com.arextest.web.model.dao.mongodb.batchcomparereport;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonSummaryConfiguration;
import com.arextest.web.model.dao.mongodb.ModelBase;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Data
@Document(collection = "BatchCompareReportCase")
public class BatchCompareReportCaseCollection extends ModelBase {

    private String planId;

    private String interfaceId;
    private String caseId;
    private String interfaceName;
    private String caseName;

    private String baseMsg;
    private String testMsg;
    private ComparisonSummaryConfiguration compareConfig;

    /**
     * @see com.arextest.web.model.contract.contracts.common.BatchCompareCaseStatusType
     */
    private int status;

    private String processedBaseMsg;
    private String processedTestMsg;
    // private List<DiffDetail> diffDetails;

    private String exceptionMsg;
}
