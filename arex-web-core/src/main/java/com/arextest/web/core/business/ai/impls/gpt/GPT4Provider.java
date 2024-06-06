package com.arextest.web.core.business.ai.impls.gpt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 18:50
 */
@Service
@EnableConfigurationProperties(GPT4Config.class)
@ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gpt4')}")
public class GPT4Provider extends ChatGPTProvider {
  public GPT4Provider(GPT4Config config) {
    super(config);
  }
}
