package com.arextest.web.api.service.beans.auto;

import com.arextest.web.api.service.interceptor.InterceptorConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(name = "interceptorConfiguration")
  public WebMvcConfigurer loadInterceptorConfiguration() {
    return new InterceptorConfiguration();
  }

}
