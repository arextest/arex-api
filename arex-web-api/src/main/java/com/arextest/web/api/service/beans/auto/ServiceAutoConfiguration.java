package com.arextest.web.api.service.beans.auto;

import com.arextest.common.utils.DefaultJWTService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceAutoConfiguration {

  private final static long ACCESS_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
  private final static long REFRESH_EXPIRE_TIME = 30 * 24 * 60 * 60 * 1000L;

  @Value("${arex.jwt.secret}")
  private String tokenSecret;


  @Bean
  @ConditionalOnMissingBean(DefaultJWTService.class)
  public DefaultJWTService defaultJWTService() {
    return new DefaultJWTService(ACCESS_EXPIRE_TIME, REFRESH_EXPIRE_TIME, tokenSecret);
  }

}
