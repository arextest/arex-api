package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FSRemoveItemRequestType {

  @NotBlank(message = "Id cannot be empty")
  private String id;
  @NotNull(message = "Remove node path cannot be empty")
  @Size(min = 1, message = "Remove path size must be greater than 0")
  private List<String> removeNodePath;
}
