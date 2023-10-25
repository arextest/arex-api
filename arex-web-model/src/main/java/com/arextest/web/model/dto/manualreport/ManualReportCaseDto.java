package com.arextest.web.model.dto.manualreport;

import java.util.List;

import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.dao.mongodb.entity.AuthDao;
import com.arextest.web.model.dao.mongodb.entity.BodyDao;
import com.arextest.web.model.dao.mongodb.entity.KeyValuePairDao;
import com.arextest.web.model.dto.filesystem.ScriptBlockDto;

import lombok.Data;

@Data
public class ManualReportCaseDto {
    private String id;
    private List<ScriptBlockDto> preRequestScripts;
    private List<ScriptBlockDto> testScripts;
    private BodyDao body;
    private List<KeyValuePairDao> headers;
    private List<KeyValuePairDao> params;
    private AuthDao auth;

    private String planItemId;
    private String caseName;
    private String baseMsg;
    private String testMsg;
    private List<LogEntity> logs;
    private Integer diffResultCode;
}
