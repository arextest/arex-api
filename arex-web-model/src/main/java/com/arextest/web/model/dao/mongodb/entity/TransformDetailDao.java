package com.arextest.web.model.dao.mongodb.entity;

import java.util.List;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@Data
public class TransformDetailDao {

  private List<String> nodePath;

  private List<TransformMethodDao> transformMethods;

  @Data
  public static class TransformMethodDao {

    private String methodName;
    private String methodArgs;
  }

}
