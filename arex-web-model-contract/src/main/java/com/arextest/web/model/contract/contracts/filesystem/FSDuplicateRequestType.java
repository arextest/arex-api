package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FSDuplicateRequestType {

  @NotBlank(message = "WorkspaceId cannot be empty")
  private String id;
  @NotNull(message = "Item path cannot be empty")
  @Size(min = 1, message = "Item path size must be greater than 0")
  private List<String> path;
}
