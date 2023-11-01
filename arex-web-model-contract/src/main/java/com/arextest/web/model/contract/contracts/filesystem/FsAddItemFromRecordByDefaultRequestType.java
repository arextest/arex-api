package com.arextest.web.model.contract.contracts.filesystem;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author b_yu
 * @since 2023/10/7
 */
@Data
public class FsAddItemFromRecordByDefaultRequestType {

  @NotBlank(message = "WorkspaceId cannot be empty")
  private String workspaceId;
  private String appName;
  private String interfaceName;
  private String nodeName;
  @NotBlank(message = "planId cannot be empty")
  private String planId;
  @NotBlank(message = "RecordId cannot be empty")
  private String recordId;
  @NotBlank(message = "operationId cannot be empty")
  private String operationId;
}
