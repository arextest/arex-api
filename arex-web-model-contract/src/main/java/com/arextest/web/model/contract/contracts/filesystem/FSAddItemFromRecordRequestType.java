package com.arextest.web.model.contract.contracts.filesystem;

import java.util.Set;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FSAddItemFromRecordRequestType {

  @NotBlank(message = "WorkspaceId cannot be empty")
  private String workspaceId;
  private String[] parentPath;
  private String nodeName;
  @NotBlank(message = "planId cannot be empty")
  private String planId;
  @NotBlank(message = "RecordId cannot be empty")
  private String recordId;
  @NotBlank(message = "operationId cannot be empty")
  private String operationId;

  private Set<String> labelIds;
}
