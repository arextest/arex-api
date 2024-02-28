package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import java.util.List;

@Data
public class FSSearchWorkspaceItemsResponseType {

  private List<FSNodeType> caseNodes;

  private List<FSNodeType> folderNodes;

  private List<FSNodeType> interfaceNodes;
}
