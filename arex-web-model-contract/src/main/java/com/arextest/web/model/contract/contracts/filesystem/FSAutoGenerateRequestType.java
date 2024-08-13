package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.model.mock.AREXMocker;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FSAutoGenerateRequestType {

  @NotBlank(message = "WorkspaceId cannot be empty")
  private String workspaceId;

  @NotEmpty(message = "Parent path cannot be empty")
  private List<String> parentPath;

  @NotBlank(message = "Node name cannot be empty")
  private String nodeName;

  List<AREXMocker> mockers;

  private String recordId;

}
