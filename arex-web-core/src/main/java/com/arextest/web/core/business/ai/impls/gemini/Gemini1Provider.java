package com.arextest.web.core.business.ai.impls.gemini;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 18:41
 */
@EnableConfigurationProperties(Gemini1Config.class)
@ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gemini1')}")
@Service
public class Gemini1Provider extends GeminiProvider {
  public Gemini1Provider(Gemini1Config config) {
    super(config);
  }
}
