package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FSExportItemRequestType {

  @NotBlank(message = "WorkspaceId cannot be empty")
  private String workspaceId;
  private List<String> path;

  /**
   * @see com.arextest.web.model.enums.ImportExportType
   */
  private int type;
}
