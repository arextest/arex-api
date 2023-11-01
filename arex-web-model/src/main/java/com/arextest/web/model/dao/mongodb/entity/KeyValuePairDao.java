package com.arextest.web.model.dao.mongodb.entity;

import lombok.Data;

@Data
public class KeyValuePairDao {

  private String key;
  private String value;
  private Boolean active;
}
