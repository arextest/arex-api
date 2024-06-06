package com.arextest.web.core.business.ai.impls.gpt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 18:50
 */
@ConfigurationProperties(prefix = "arex.ai.gpt4")
public class GPT4Config extends TokenGPTConfig {

}
