package com.arextest.web.core.business.ai.impls.aws.claude;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: QizhengMo
 * @date: 2024/6/18 14:30
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "arex.ai.claude-haiku")
public class HaikuConfig extends ClaudeConfig {
}
