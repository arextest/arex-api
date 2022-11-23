package com.arextest.report.model.api.contracts.environment;

import com.arextest.report.model.api.contracts.common.KeyValuePairType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class EnvironmentType {
    private String id;
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    @NotBlank(message = "Environment name cannot be empty")
    private String envName;
    private List<KeyValuePairType> keyValues;
}
