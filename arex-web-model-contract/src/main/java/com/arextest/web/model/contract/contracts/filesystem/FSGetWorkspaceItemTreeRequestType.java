package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;


@Data
public class FSGetWorkspaceItemTreeRequestType {

  private String workspaceId;

  private String infoId;

  private int nodeType;
}
