package com.arextest.web.api.service.beans.auto;

import com.arextest.common.jwt.JWTService;
import com.arextest.common.jwt.JWTServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceAutoConfiguration {

  private static final long ACCESS_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
  private static final long REFRESH_EXPIRE_TIME = 30 * 24 * 60 * 60 * 1000L;

  @Value("${arex.jwt.secret}")
  private String tokenSecret;


  @Bean
  @ConditionalOnMissingBean(JWTService.class)
  public JWTService jwtService() {
    return new JWTServiceImpl(ACCESS_EXPIRE_TIME, REFRESH_EXPIRE_TIME, tokenSecret);
  }

}
