package com.arextest.web.model.dao.mongodb;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;

@Data
@FieldNameConstants
public class ModelBase {

  @Id
  private String id;
  private Long dataChangeCreateTime;
  private Long dataChangeUpdateTime;
}
