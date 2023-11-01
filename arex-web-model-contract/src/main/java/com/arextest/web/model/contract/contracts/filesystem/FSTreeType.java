package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import lombok.Data;

@Data
public class FSTreeType {

  private String id;
  private String workspaceName;
  private String userName;
  private List<FSNodeType> roots;
}
