package com.arextest.web.model.contract.contracts.config.replay;

import com.arextest.web.model.contract.PagingRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class PageQueryComparisonRequestType implements PagingRequest {

  @NotBlank
  private String appId;

  private List<String> operationIds;

  private List<String> dependencyIds;

  @NotNull
  private Integer pageIndex;
  @NotNull
  private Integer pageSize;
  private Boolean needTotal;
}
