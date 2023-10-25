package com.arextest.web.model.dto.manualreport;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.enums.DiffResultCode;

import lombok.Data;

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
