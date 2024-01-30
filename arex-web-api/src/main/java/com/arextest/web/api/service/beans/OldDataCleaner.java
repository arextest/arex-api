package com.arextest.web.api.service.beans;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.cache.LockWrapper;
import com.arextest.web.model.dao.mongodb.ConfigComparisonIgnoreCategoryCollection;
import com.arextest.web.model.dao.mongodb.entity.CategoryDetailDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wildeslam.
 * @create 2024/1/26 11:00
 */
@Configuration
@Lazy(false)
@Slf4j
@ConditionalOnMissingBean(name = "oldDataCleaner")
public class OldDataCleaner {

    @Resource
    private CacheProvider cacheProvider;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${arex.api.redis.lease-time}")
    private long redisLeaseTime;

    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(this::cleanOldData);
    }

    public void cleanOldData() {
        cleanConfigComparisonIgnoreCategoryCollection(this.mongoTemplate);
    }

    // Collection ConfigComparisonIgnoreCategory's structure has been changed, need to transfer old data to new.
    // New data was introduced at 0.6.0.17, this method was introduced at 0.6.0.20
    private void cleanConfigComparisonIgnoreCategoryCollection(MongoTemplate mongoTemplate) {
        LockWrapper lock = cacheProvider.getLock("ConfigComparisonIgnoreCategory");
        try {
            lock.lock(redisLeaseTime, TimeUnit.SECONDS);
            Query query = Query.query(Criteria.where(ConfigComparisonIgnoreCategoryCollection.Fields.ignoreCategory).ne(null));
            List<ConfigComparisonIgnoreCategoryCollection> oldData =
                mongoTemplate.findAllAndRemove(query, ConfigComparisonIgnoreCategoryCollection.class).stream()
                    .filter(config -> CollectionUtils.isNotEmpty(config.getIgnoreCategory()))
                    .collect(Collectors.toList());

            List<ConfigComparisonIgnoreCategoryCollection> newData = new ArrayList<>();
            oldData.forEach(oldConfig -> {
                oldConfig.getIgnoreCategory().forEach(ignoreCategory -> {
                    ConfigComparisonIgnoreCategoryCollection newConfigItem = convertToNewConfig(oldConfig);
                    CategoryDetailDao categoryDetailDao = new CategoryDetailDao();
                    categoryDetailDao.setOperationType(ignoreCategory);
                    newConfigItem.setIgnoreCategoryDetail(categoryDetailDao);

                    newData.add(newConfigItem);
                });
            });
            mongoTemplate.insertAll(newData);
        } catch (Exception e) {
            LOGGER.error("Failed to clean  data for ConfigComparisonIgnoreCategoryCollection", e);
        } finally {
            lock.unlock();
        }

    }

    private ConfigComparisonIgnoreCategoryCollection convertToNewConfig(
        ConfigComparisonIgnoreCategoryCollection oldConfig) {
        ConfigComparisonIgnoreCategoryCollection newConfig = new ConfigComparisonIgnoreCategoryCollection();
        newConfig.setDataChangeCreateTime(System.currentTimeMillis());
        newConfig.setDataChangeUpdateTime(System.currentTimeMillis());
        newConfig.setAppId(oldConfig.getAppId());
        newConfig.setOperationId(oldConfig.getOperationId());
        newConfig.setExpirationType(oldConfig.getExpirationType());
        newConfig.setExpirationDate(oldConfig.getExpirationDate());
        return newConfig;
    }
}
