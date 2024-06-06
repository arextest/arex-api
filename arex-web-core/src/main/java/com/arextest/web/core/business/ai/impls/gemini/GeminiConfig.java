package com.arextest.web.core.business.ai.impls.gemini;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 18:37
 */
@ConfigurationProperties(prefix = "arex.ai.gemini1")
@Getter
@Setter
public class GeminiConfig {
  private String projectId;
  private String modelName;
  private Integer maxToken;
}