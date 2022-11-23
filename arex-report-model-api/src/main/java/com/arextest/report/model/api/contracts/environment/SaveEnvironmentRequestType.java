package com.arextest.report.model.api.contracts.environment;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;

@Data
public class SaveEnvironmentRequestType {
    @NotNull(message = "Env cannot be empty")
    private EnvironmentType env;
}
