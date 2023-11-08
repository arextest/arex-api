package com.arextest.web.api.service.beans;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.cache.DefaultRedisCacheProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author b_yu
 * @since 2023/4/6
 */
@Configuration
public class RedisConfiguration {

  @Value("${arex.redis.uri}")
  private String redisUri;

  @Bean
  @ConditionalOnMissingBean(CacheProvider.class)
  public CacheProvider cacheProvider() {
    return new DefaultRedisCacheProvider(redisUri);
  }
}
