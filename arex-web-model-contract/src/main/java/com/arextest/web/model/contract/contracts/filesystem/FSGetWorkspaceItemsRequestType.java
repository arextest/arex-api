package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSGetWorkspaceItemsRequestType {

  private String workspaceId;

  private String parentInfoId;
  private Integer parentNodeType;
}
