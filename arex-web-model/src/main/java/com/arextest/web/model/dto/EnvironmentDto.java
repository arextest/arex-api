package com.arextest.web.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class EnvironmentDto {
    private String id;
    private String workspaceId;
    private String envName;
    private List<KeyValuePairDto> keyValues;
}
