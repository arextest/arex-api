package com.arextest.web.model.contract.contracts.vertexai;

import lombok.Data;

/**
 * @author: QizhengMo
 * @date: 2024/3/25 15:40
 */
@Data
public class GenReq {
  private String currentScript;
  private String requirement;

  private String apiRes;
}