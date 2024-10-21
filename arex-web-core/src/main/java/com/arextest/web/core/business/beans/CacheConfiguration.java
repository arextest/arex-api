package com.arextest.web.core.business.beans;

import com.arextest.web.core.business.preprocess.PreprocessTreeCacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author b_yu
 * @since 2022/6/2
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

  @Resource
  public PreprocessTreeCacheLoader cacheLoader;

  @Bean(name = "schemaCache")
  public LoadingCache schemaCache() {
    return Caffeine.newBuilder().recordStats().maximumSize(1000)
        .refreshAfterWrite(10, TimeUnit.SECONDS)
        .expireAfterWrite(15, TimeUnit.SECONDS).build(cacheLoader);
  }
}
