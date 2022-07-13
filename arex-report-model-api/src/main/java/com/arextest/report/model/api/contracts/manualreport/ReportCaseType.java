package com.arextest.report.model.api.contracts.manualreport;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import com.arextest.report.model.api.contracts.filesystem.AuthType;
import com.arextest.report.model.api.contracts.filesystem.BodyType;
import lombok.Data;

import java.util.List;

@Data
public class ReportCaseType {
    private String id;
    private String preRequestScript;
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
