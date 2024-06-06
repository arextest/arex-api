package com.arextest.web.core.business.ai.impls.gpt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: QizhengMo
 * @date: 2024/6/6 18:49
 */
@ConfigurationProperties(prefix = "arex.ai.gpt35")
public class GPT35Config extends TokenGPTConfig {

}
