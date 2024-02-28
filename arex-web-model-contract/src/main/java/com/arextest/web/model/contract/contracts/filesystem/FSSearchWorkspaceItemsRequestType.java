package com.arextest.web.model.contract.contracts.filesystem;

import com.arextest.web.model.contract.PagingRequest;
import lombok.Data;

import java.util.List;

@Data
public class FSSearchWorkspaceItemsRequestType implements PagingRequest {

  private String workspaceId;

  private String keywords;

  private List<LabelType> labels;

  private Integer pageIndex;
  private Integer pageSize;
  private Boolean needTotal;
}
