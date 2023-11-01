package com.arextest.web.model.dto.filesystem.importexport;

import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class FolderItemDto implements Item {

  private String name;
  private String nodeName;
  private Integer nodeType;
  private Set<String> labelIds;
  private List<Item> items;
}
