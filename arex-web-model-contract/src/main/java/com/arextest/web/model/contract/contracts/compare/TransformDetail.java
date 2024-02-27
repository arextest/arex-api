package com.arextest.web.model.contract.contracts.compare;

import java.util.List;
import lombok.Data;

@Data
public class TransformDetail {

  List<String> nodePath;

  List<TransformMethod> transformMethods;

  @Data
  public static class TransformMethod {

    private String methodName;
    private String methodArgs;
  }

}
