package com.arextest.web.core.business.ai.impls.gpt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 18:48
 */
@Service
@ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gpt35')}")
@EnableConfigurationProperties(GPT35Config.class)
public class GPT35Provider extends ChatGPTProvider {
  public GPT35Provider(GPT35Config config) {
    super(config);
  }
}
