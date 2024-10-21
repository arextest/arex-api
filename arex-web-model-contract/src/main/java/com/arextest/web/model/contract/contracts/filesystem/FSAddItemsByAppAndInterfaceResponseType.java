package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author b_yu
 * @since 2024/2/6
 */
@Data
public class FSAddItemsByAppAndInterfaceResponseType {
  private Boolean success;
  private List<String> path;
  private String workspaceId;
}
