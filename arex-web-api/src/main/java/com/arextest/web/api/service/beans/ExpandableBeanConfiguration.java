package com.arextest.web.api.service.beans;

import com.arextest.common.cache.CacheProvider;
import com.arextest.config.repository.impl.SystemConfigurationRepositoryImpl;
import com.arextest.web.core.business.ConfigLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author wildeslam.
 * @create 2024/1/26 11:00
 */
@Configuration
@Lazy(false)
@Slf4j
public class ExpandableBeanConfiguration {

  @Lazy(false)
  @Bean
  @ConditionalOnMissingBean(name = "oldDataCleaner")
  public OldDataCleaner oldDataCleaner(CacheProvider cacheProvider,
      MongoTemplate mongoTemplate, SystemConfigurationRepositoryImpl systemConfigurationRepository,
      @Value("${arex.api.redis.lease-time}") long redisLeaseTime) {
    return new OldDataCleaner(cacheProvider, mongoTemplate, redisLeaseTime, systemConfigurationRepository);
  }

  @Lazy(false)
  @Bean
  @ConditionalOnMissingBean(name = "systemConfigBootstrap")
  public SystemConfigBootstrap systemConfigBootstrap(
      ConfigLoadService configLoadService,
      SystemConfigurationRepositoryImpl systemConfigurationRepository) {
    return new SystemConfigBootstrap(configLoadService, systemConfigurationRepository);
  }

}
