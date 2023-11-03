package com.arextest.web.core.business.filesystem.importexport.postmancollection;

import java.util.List;
import lombok.Data;

@Data
public class ItemResponse {

  private String name;
  private ItemRequest originalRequest;
  private String status;
  private Integer code;
  private String _postman_previewlanguage;
  private List<ItemHeader> header;
  private String body;
}
