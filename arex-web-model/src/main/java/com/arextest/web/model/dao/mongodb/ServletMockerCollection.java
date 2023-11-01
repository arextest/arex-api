package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ServletMocker")
public class ServletMockerCollection extends ModelBase {

  private String appId;
  private String path;
  private String request;
  private String response;
}
