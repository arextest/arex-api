package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import lombok.Data;


@Data
public class FSGetWorkspaceItemsResponseType {

  private FSNodeType node;
  private List<String> path;
}
