package com.arextest.web.model.contract.contracts.label;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author b_yu
 * @since 2022/11/21
 */
@Data
public class RemoveLabelRequestType {

  @NotBlank(message = "Label Id cannot be empty")
  private String id;
  @NotBlank(message = "Workspace Id cannot be empty")
  private String workspaceId;
}
