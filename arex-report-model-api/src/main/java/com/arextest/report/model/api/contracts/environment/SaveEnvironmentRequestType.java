package com.arextest.report.model.api.contracts.environment;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

@Data
public class SaveEnvironmentRequestType {
    private EnvironmentType env;
}
