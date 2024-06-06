package com.arextest.web.core.business.ai.impls.gemini;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 18:41
 */
@EnableConfigurationProperties(Gemini15Config.class)
@ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gemini15')}")
public class Gemini15Provider extends GeminiProvider {
  public Gemini15Provider(Gemini15Config config) {
    super(config);
  }
}
