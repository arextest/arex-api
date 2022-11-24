package com.arextest.web.model.dto.manualreport;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.enums.DiffResultCode;
import lombok.Data;

import java.util.List;

@Data
public class SaveManualReportCaseDto {
    private String id;
    private String baseMsg;
    private String testMsg;
    private List<LogEntity> logs;
    /**
     * {@link DiffResultCode}
     */
    private Integer diffResultCode;
}
