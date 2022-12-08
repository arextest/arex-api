package com.arextest.web.model.contract.contracts.manualreport;

import com.arextest.web.model.contract.contracts.common.KeyValuePairType;
import com.arextest.web.model.contract.contracts.common.ScriptBlockType;
import com.arextest.web.model.contract.contracts.filesystem.AuthType;
import com.arextest.web.model.contract.contracts.filesystem.BodyType;
import lombok.Data;

import java.util.List;

@Data
public class ReportCaseType {
    private String id;
    private List<ScriptBlockType> preRequestScripts;
    private String testScript;
    private BodyType body;
    private List<KeyValuePairType> headers;
    private List<KeyValuePairType> params;
    private AuthType auth;

    private String planItemId;
    private String caseName;
    private String baseMsg;
    private String testMsg;
    private String logs;
    private Integer diffResultCode;
}
