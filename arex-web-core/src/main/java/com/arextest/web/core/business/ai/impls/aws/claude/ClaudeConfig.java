package com.arextest.web.core.business.ai.impls.aws.claude;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: QizhengMo
 * @date: 2024/6/18 14:30
 */
@Getter
@Setter
public class ClaudeConfig {
  private String region;
  private String modelId;
}
