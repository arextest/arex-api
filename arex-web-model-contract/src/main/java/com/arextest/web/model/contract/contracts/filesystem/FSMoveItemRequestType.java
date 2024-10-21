package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FSMoveItemRequestType {

  @NotBlank(message = "WorkspaceId cannot be empty")
  private String id;
  @NotNull(message = "From node path cannot be empty")
  @Size(message = "Source item cannot be empty")
  private List<String> fromNodePath;
  private List<String> toParentPath;
  private Integer toIndex;
}
