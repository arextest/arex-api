package com.arextest.web.model.contract.contracts;

import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/9/25 19:52
 */
@Data
public class CallbackInformRequestType {

  private String appId;
  private String appName;
  private String planName;
  private Integer status;
  private Integer totalCaseCount;
  private Integer successCaseCount;
  private Integer failCaseCount;
  private Integer errorCaseCount;
  private Integer waitCaseCount;
  private Double passRate;
  private Long elapsedMillSeconds;
  private String creator;
}
