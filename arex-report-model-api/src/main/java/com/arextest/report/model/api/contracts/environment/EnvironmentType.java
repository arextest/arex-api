package com.arextest.report.model.api.contracts.environment;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import lombok.Data;

import java.util.List;

@Data
public class EnvironmentType {
    private String id;
    private String workspaceId;
    private String envName;
    private List<KeyValuePairType> keyValues;
}
