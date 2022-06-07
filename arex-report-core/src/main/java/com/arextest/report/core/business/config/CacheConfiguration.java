package com.arextest.report.core.business.config;

import com.arextest.report.core.business.preprocess.PreprocessTreeCacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        return Caffeine.newBuilder()
                .recordStats()
                .maximumSize(1000)
                .refreshAfterWrite(10, TimeUnit.SECONDS)
                .expireAfterWrite(15, TimeUnit.SECONDS)
                .build(cacheLoader);
    }
}
