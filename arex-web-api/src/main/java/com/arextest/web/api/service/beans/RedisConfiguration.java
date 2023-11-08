package com.arextest.web.api.service.beans;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.cache.DefaultDistributedLockProvider;
import com.arextest.common.cache.DistributedLockProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author b_yu
 * @since 2023/4/6
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(DistributedLockProvider.class)
public class RedisConfiguration {

  @Value("${arex.redis.uri}")
  private String redisUri;

  @Bean
  public DistributedLockProvider<RLock> distributedLockProvider() {
    return new DefaultDistributedLockProvider(redisUri);
  }
}
