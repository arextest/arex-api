package com.arextest.web.model.dao.mongodb.entity;

import lombok.Data;

/**
 * @author b_yu
 * @since 2022/12/8
 */
@Data
public class ScriptBlockDao {

  private String type;
  private String icon;
  private String label;
  private String value;
  private String description;
  private Boolean disabled;
}
