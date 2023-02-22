package com.arextest.web.model.dto.batchcomparereport;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.dto.BaseDto;
import lombok.Data;

/**
 * Created by rchen9 on 2023/2/9.
 */
@Data
public class BatchCompareReportResultDto extends BaseDto {

    private String planId;
    private String interfaceId;
    private String caseId;

    private int unMatchedType;
    private String fuzzyPath;

    private LogEntity logEntity;

}
