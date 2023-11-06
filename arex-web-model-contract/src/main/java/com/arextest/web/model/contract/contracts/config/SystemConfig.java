package com.arextest.web.model.contract.contracts.config;

import java.util.Set;
import lombok.Data;

/**
 * @author wildeslam.
 * @create 2023/9/25 16:57
 */
@Data
public class SystemConfig {

  private String operator;

  /**
   * for callBackInform.
   */
  private String callbackUrl;
  /**
   * control the compare precision of the time field.
   */
  private Long compareIgnoreTimePrecisionMillis;
  /**
   * ignore the case, when comparing
   */
  private Boolean compareNameToLower;
  /**
   * the null and '' think unanimously, when comparing
   */
  private Boolean compareNullEqualsEmpty;

  private Set<String> ignoreNodeSet;
}
