package com.arextest.web.model.contract.contracts.batchcomparereport;

import com.arextest.web.model.contract.contracts.config.replay.ComparisonSummaryConfiguration;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Data
public class UpdateBatchCompareCaseRequestType {

    @NotBlank(message = "planId cannot be empty")
    private String planId;
    @NotBlank(message = "interfaceId cannot be empty")
    private String interfaceId;
    @NotBlank(message = "caseId cannot be empty")
    private String caseId;

    private String baseMsg;
    private String testMsg;
    private ComparisonSummaryConfiguration comparisonConfig;

    private String exceptionMsg;

}
