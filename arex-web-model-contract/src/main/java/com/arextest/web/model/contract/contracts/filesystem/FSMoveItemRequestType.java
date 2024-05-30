package com.arextest.web.model.contract.contracts.filesystem;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class FSMoveItemRequestType {

  @NotBlank(message = "WorkspaceId cannot be empty")
  private String id;
  @NotBlank(message = "fromInfoId cannot be empty")
  private String fromInfoId;
  @NotNull(message = "fromNodeType cannot be empty")
  @Min(value = 1, message = "fromNodeType must be greater than or equal to 1")
  @Max(value = 3, message = "fromNodeType must be less than or equal to 3")
  private Integer fromNodeType;

  private String toParentInfoId;
  private Integer toParentNodeType;
  private Integer toIndex;
}
