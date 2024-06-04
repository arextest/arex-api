package com.arextest.web.core.business.ai;

import com.arextest.web.core.business.ai.impls.ChatGPTProvider;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arextest.web.core.business.ai.impls.GeminiProvider;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: QizhengMo
 * @date: 2024/6/4 10:55
 */
@Configuration
@EnableConfigurationProperties({AIConfiguration.Gemini1Config.class,
    AIConfiguration.Gemini15Config.class,
    AIConfiguration.GPT40Config.class,
    AIConfiguration.GPT35Config.class})
public class AIConfiguration {

  @ConfigurationProperties(prefix = "arex.ai.gemini1")
  @Getter
  @Setter
  public static class Gemini1Config {
    private String projectId;
    private String modelName;
    private Integer maxToken;
  }

  @Bean
  @ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gemini1')}")
  public AIProvider gemini1(Gemini1Config config) {
    return new GeminiProvider(config.getProjectId(), config.getModelName(), config.getMaxToken());
  }

  @ConfigurationProperties(prefix = "arex.ai.gemini15")
  @Getter
  @Setter
  public static class Gemini15Config extends Gemini1Config {}

  @Bean
  @ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gemini15')}")
  public AIProvider gemini15(Gemini15Config config) {
    return new GeminiProvider(config.getProjectId(), config.getModelName(), config.getMaxToken());
  }


  @ConfigurationProperties(prefix = "arex.ai.gpt35")
  @Getter
  @Setter
  public static class GPT35Config {
    private String tenantId;
    private String clientId;
    private String clientSecret;
    private String endpoint;
    private String model;
    private Integer maxToken;
  }

  @Bean
  @ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gpt35')}")
  public AIProvider gpt35(GPT35Config config) {
    return new ChatGPTProvider(config.getTenantId(),
        config.getClientId(),
        config.getClientSecret(),
        config.getEndpoint(),
        config.getModel(),
        config.getMaxToken(),
        Optional.empty());
  }

  @ConfigurationProperties(prefix = "arex.ai.gpt40")
  @Getter
  @Setter
  public static class GPT40Config extends GPT35Config {}

  @Bean
  @ConditionalOnExpression("#{systemProperties['arex.ai.providers'].contains('gpt40')}")
  public AIProvider gpt40(GPT40Config config) {
    return new ChatGPTProvider(config.getTenantId(),
        config.getClientId(),
        config.getClientSecret(),
        config.getEndpoint(),
        config.getModel(),
        config.getMaxToken(),
        Optional.empty());
  }
}
