package com.arextest.web.model.contract.contracts.batchcomparereport;

import lombok.Data;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/9.
 */
@Data
public class BatchCompareInterfaceProcess {
    private String interfaceId;
    private String interfaceName;
    private List<StatusStatistic> statusList;

    @Data
    public static class StatusStatistic {
        /**
         * @see com.arextest.web.model.contract.contracts.common.BatchCompareCaseStatusType
         */
        private int status;
        private int count;
        private List<String> caseIdList;
    }
}
