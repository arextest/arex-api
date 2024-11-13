package com.arextest.web.model.dto.config;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class PageQueryComparisonDto {

  @NotBlank
  private String appId;

  private List<String> operationIds;

  private List<String> dependencyIds;

  private Integer pageIndex;
  private Integer pageSize;
  private Boolean needTotal;
}
