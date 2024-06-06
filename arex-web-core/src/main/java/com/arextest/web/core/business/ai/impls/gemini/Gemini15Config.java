package com.arextest.web.core.business.ai.impls.gemini;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 18:37
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "arex.ai.gemini15")
public class Gemini15Config extends GeminiConfig {
}