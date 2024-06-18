package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class FSRenameRequestType {

  @NotBlank(message = "Id cannot be empty")
  private String id;
  @NotNull(message = "Node path cannot be empty")
  @Size(min = 1, message = "Path size must be greater than 0")
  private List<String> path;
  @NotBlank(message = "New name cannot be empty")
  private String newName;
}
