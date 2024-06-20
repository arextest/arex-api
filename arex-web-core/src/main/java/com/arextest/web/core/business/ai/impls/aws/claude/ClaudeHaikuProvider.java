package com.arextest.web.core.business.ai.impls.aws.claude;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author: QizhengMo
 * @date: 2024/6/18 14:29
 */
@EnableConfigurationProperties(HaikuConfig.class)
@ConditionalOnExpression("#{'${arex.ai.providers:{}}'.contains('claude-haiku')}")
@Service
public class ClaudeHaikuProvider extends ClaudeProvider {
  public ClaudeHaikuProvider(HaikuConfig config) {
    super(config);
  }
}
