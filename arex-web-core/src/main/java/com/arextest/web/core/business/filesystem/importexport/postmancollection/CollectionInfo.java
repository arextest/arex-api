package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import lombok.Data;

@Data
public class CollectionInfo {

  private String _postman_id;
  private String name;
  private String description;
  private String schema;
}
