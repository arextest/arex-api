package com.arextest.web.model.contract.contracts.label;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author b_yu
 * @since 2022/11/22
 */
@Data
public class QueryLabelsByWorkspaceIdRequestType {

  @NotBlank(message = "Workspace id cannot be empty")
  private String workspaceId;
}
