package com.arextest.web.core.repository.mongo.util;

import com.arextest.web.model.dao.mongodb.ConfigComparisonIgnoreCategoryCollection;
import com.arextest.web.model.dao.mongodb.entity.CategoryDetailDao;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author wildeslam.
 * @create 2024/1/26 11:00
 */
@Repository
@Lazy(false)
public class OldDataCleaner {
    private final MongoTemplate mongoTemplate;

    public OldDataCleaner(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void main() {
        CompletableFuture.runAsync(this::cleanOldData);
    }

    public void cleanOldData() {
        cleanConfigComparisonIgnoreCategoryCollection(this.mongoTemplate);
    }

    private void cleanConfigComparisonIgnoreCategoryCollection(MongoTemplate mongoTemplate) {
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
    }

    private ConfigComparisonIgnoreCategoryCollection convertToNewConfig(
        ConfigComparisonIgnoreCategoryCollection oldConfig) {
        ConfigComparisonIgnoreCategoryCollection newConfig = new ConfigComparisonIgnoreCategoryCollection();
        MongoHelper.initInsertObject(newConfig);
        newConfig.setAppId(oldConfig.getAppId());
        newConfig.setOperationId(oldConfig.getOperationId());
        newConfig.setExpirationType(oldConfig.getExpirationType());
        newConfig.setExpirationDate(oldConfig.getExpirationDate());
        return newConfig;
    }
}
