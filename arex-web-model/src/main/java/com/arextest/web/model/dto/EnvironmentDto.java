package com.arextest.web.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class EnvironmentDto {

  private String id;
  private String workspaceId;
  private String envName;
  private List<KeyValuePairDto> keyValues;
}
