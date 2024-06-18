package com.arextest.web.core.business.ai.impls.aws.claude;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: QizhengMo
 * @date: 2024/6/18 14:30
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "arex.ai.claude-sonnet")
public class SonnetConfig extends ClaudeConfig {
}
