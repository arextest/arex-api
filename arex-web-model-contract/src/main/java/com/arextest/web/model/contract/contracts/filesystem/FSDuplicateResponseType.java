package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import lombok.Data;

/**
 * @author b_yu
 * @since 2024/2/23
 */
@Data
public class FSDuplicateResponseType {
  private Boolean success;
  private String infoId;
  private String workspaceId;
  private List<String> path;
}
