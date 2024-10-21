package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FSExportItemRequestType {

  @NotBlank(message = "WorkspaceId cannot be empty")
  private String workspaceId;
  private List<String> path;

  /**
   * com.arextest.web.model.enums.ImportExportType
   */
  private int type;
}
