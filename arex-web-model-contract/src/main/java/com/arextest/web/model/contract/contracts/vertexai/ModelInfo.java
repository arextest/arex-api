package com.arextest.web.model.contract.contracts.vertexai;

import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/6/4 10:54
 */
@Data
public class ModelInfo {
  private String modelName;
  private Integer tokenLimit;
}
