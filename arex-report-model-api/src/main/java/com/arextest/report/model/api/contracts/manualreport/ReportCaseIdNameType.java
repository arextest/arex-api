package com.arextest.report.model.api.contracts.manualreport;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import com.arextest.report.model.api.contracts.filesystem.AuthType;
import com.arextest.report.model.api.contracts.filesystem.BodyType;
import lombok.Data;

import java.util.List;

@Data
public class ReportCaseIdNameType {
    private String id;
    private String caseName;
}
