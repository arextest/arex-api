package com.arextest.web.model.contract.contracts.filesystem;

import java.util.List;
import lombok.Data;

@Data
public class FSRemoveItemResponseType {

  private Boolean success;
  private List<String> path;
}
