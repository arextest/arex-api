package com.arextest.web.model.contract.contracts.batchcomparereport;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Data
public class BatchCompareReportRequestType {
    @NotBlank(message = "planId cannot be empty")
    private String planId;

    @NotEmpty(message = "the list of compared case cannot be empty")
    private List<@Valid BatchCompareCase> batchCompareCaseList;

    @Data
    public static class BatchCompareCase {
        @NotBlank(message = "interfaceId cannot be empty")
        private String interfaceId;
        @NotBlank(message = "caseId cannot be empty")
        private String caseId;

        private String interfaceName;
        private String caseName;
    }
}
