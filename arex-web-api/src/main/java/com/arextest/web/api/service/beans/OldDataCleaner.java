package com.arextest.web.api.service.beans;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.cache.LockWrapper;
import com.arextest.config.model.dao.config.AppCollection;
import com.arextest.config.model.dao.config.RecordServiceConfigCollection;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.common.DesensitizationJar;
import com.arextest.web.model.dao.mongodb.ConfigComparisonIgnoreCategoryCollection;
import com.arextest.web.model.dao.mongodb.DesensitizationJarCollection;
import com.arextest.web.model.dao.mongodb.ModelBase;
import com.arextest.web.model.dao.mongodb.SystemConfigCollection;
import com.arextest.web.model.dao.mongodb.SystemConfigurationCollection;
import com.arextest.web.model.dao.mongodb.entity.CategoryDetailDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.arextest.web.model.dao.mongodb.SystemConfigurationCollection.KeySummary.CALLBACK_URL;
import static com.arextest.web.model.dao.mongodb.SystemConfigurationCollection.KeySummary.DESERIALIZATION_JAR;
import static com.arextest.web.model.dao.mongodb.SystemConfigurationCollection.KeySummary.REFRESH_DATA;

@Data
@Slf4j
@AllArgsConstructor
public class OldDataCleaner implements InitializingBean {

  private CacheProvider cacheProvider;
  private MongoTemplate mongoTemplate;
  private long redisLeaseTime;

  @Override
  public void afterPropertiesSet() {
    CompletableFuture.runAsync(transferSystemConfigTask());
    CompletableFuture.runAsync(cleanConfigComparisonIgnoreCategoryCollection());
    CompletableFuture.runAsync(buildAddMissingRecordServiceConfigTask());
  }

  /**
   * Collection ConfigComparisonIgnoreCategory's structure has been changed, need to transfer old
   * data to new. New data was introduced at 0.6.0.17, this method was introduced at 0.6.0.20
   *
   * @return
   */
  private Runnable cleanConfigComparisonIgnoreCategoryCollection() {
    Consumer<RefreshTaskContext> task = (RefreshTaskContext refreshTaskContext) -> {
      if (isTaskFinish(refreshTaskContext)) {
        LOGGER.info("skip cleanConfigComparisonIgnoreCategoryCollection");
        return;
      }

      LOGGER.info("start clean data for ConfigComparisonIgnoreCategoryCollection");
      Query query = Query.query(
          Criteria.where(ConfigComparisonIgnoreCategoryCollection.Fields.ignoreCategory).ne(null));
      List<ConfigComparisonIgnoreCategoryCollection> oldData =
          mongoTemplate.findAllAndRemove(query, ConfigComparisonIgnoreCategoryCollection.class)
              .stream()
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

      // set completion position flag
      markTaskFinish(refreshTaskContext);
      LOGGER.info("finish clean data for ConfigComparisonIgnoreCategoryCollection");
    };

    RefreshTaskContext refreshTaskContext = new RefreshTaskContext(mongoTemplate,
        RefreshTaskName.CLEAN_CONFIG_COMPARISON_IGNORE_CATEGORY);

    return new LockRefreshTask<>(
        cacheProvider, redisLeaseTime, task,
        refreshTaskContext);
  }

  /**
   * Change the generation timing of RecordServiceConfig: from modification to generation when the
   * application is created. Refresh data that was previously created but no RecordServiceConfig was
   * generated
   * <p>
   * this method was introduced at 0.6.0.21
   *
   * @return
   */
  private Runnable buildAddMissingRecordServiceConfigTask() {

    Consumer<RefreshTaskContext> task = (RefreshTaskContext refreshTaskContext) -> {
      if (isTaskFinish(refreshTaskContext)) {
        LOGGER.info("skip addMissingRecordServiceConfigTask");
        return;
      }

      LOGGER.info("start addMissingRecordServiceConfigTask");
      LookupOperation lookupOperation = LookupOperation.newLookup()
          .from(RecordServiceConfigCollection.DOCUMENT_NAME)
          .localField(RecordServiceConfigCollection.Fields.appId)
          .foreignField(RecordServiceConfigCollection.Fields.appId)
          .as("config");

      MatchOperation matchOperation = Aggregation.match(
          Criteria.where("config").is(Collections.emptyList()));
      ProjectionOperation projectionOperation = Aggregation.project(
          RecordServiceConfigCollection.Fields.appId);
      Aggregation aggregation = Aggregation.newAggregation(lookupOperation, matchOperation,
          projectionOperation);
      List<Document> results = mongoTemplate.aggregate(aggregation, AppCollection.DOCUMENT_NAME,
          Document.class).getMappedResults();

      List<Document> documents = new ArrayList<>();
      for (Document result : results) {
        Document temp = new Document();
        temp.put(RecordServiceConfigCollection.Fields.appId,
            result.getString(RecordServiceConfigCollection.Fields.appId));
        temp.put(RecordServiceConfigCollection.Fields.allowDayOfWeeks, 127);
        temp.put(RecordServiceConfigCollection.Fields.allowTimeOfDayFrom, "00:01");
        temp.put(RecordServiceConfigCollection.Fields.allowTimeOfDayTo, "23:59");
        temp.put(RecordServiceConfigCollection.Fields.sampleRate, 1);
        temp.put(RecordServiceConfigCollection.Fields.timeMock, true);
        temp.put(RecordServiceConfigCollection.Fields.recordMachineCountLimit, 1);
        temp.put(ModelBase.Fields.dataChangeCreateTime, System.currentTimeMillis());
        temp.put(ModelBase.Fields.dataChangeUpdateTime, System.currentTimeMillis());
        documents.add(temp);
      }

      if (CollectionUtils.isNotEmpty(documents)) {
        LOGGER.info("start addMissingRecordServiceConfigTask, size:{}", documents.size());
        mongoTemplate.getDb().getCollection(RecordServiceConfigCollection.DOCUMENT_NAME).insertMany(
            documents
        );
      }

      // set completion position flag
      markTaskFinish(refreshTaskContext);
      LOGGER.info("finish addMissingRecordServiceConfigTask");
    };

    RefreshTaskContext refreshTaskContext = new RefreshTaskContext(mongoTemplate,
        RefreshTaskName.BUILD_ADD_MISSING_RECORD_SERVICE_CONFIG);

    return new LockRefreshTask<>(cacheProvider, redisLeaseTime, task,
        refreshTaskContext);
  }

  /**
   * Transfer DesensitizationJarCollection and SystemConfigCollection to SystemConfigurationCollection
   * Introduced at 0.6.2.2
   */
  private Runnable transferSystemConfigTask() {
    Consumer<RefreshTaskContext> task = (RefreshTaskContext refreshTaskContext) -> {
      if (isTaskFinish(refreshTaskContext)) {
        LOGGER.info("skip transferSystemConfigTask");
        return;
      }

      LOGGER.info("start transferSystemConfigTask");
      Query systemConfigQuery = new Query();
      systemConfigQuery.with(Sort.by(Sort.Direction.DESC, ModelBase.Fields.dataChangeCreateTime));
      systemConfigQuery.limit(1);
      SystemConfigCollection latestSystemConfig = mongoTemplate.findOne(systemConfigQuery,
          SystemConfigCollection.class);

      DesensitizationJarCollection desensitizationJarCollection = mongoTemplate.findOne(
          new Query(), DesensitizationJarCollection.class);

      //drop old collection
      mongoTemplate.dropCollection(SystemConfigCollection.class);
      mongoTemplate.dropCollection(DesensitizationJarCollection.class);

      if (latestSystemConfig != null) {
        SystemConfigurationCollection callbackConfig = new SystemConfigurationCollection();
        callbackConfig.setCallbackUrl(latestSystemConfig.getCallbackUrl());
        callbackConfig.setKey(CALLBACK_URL);

        Update update = MongoHelper.getUpdate();
        MongoHelper.appendFullProperties(update, callbackConfig);
        mongoTemplate.upsert(Query.query(Criteria.where(SystemConfigurationCollection.Fields.key).is(CALLBACK_URL)),
            update, SystemConfigurationCollection.class);
      }

      if (desensitizationJarCollection != null) {
        SystemConfigurationCollection desensitizationJarConfig = new SystemConfigurationCollection();
        desensitizationJarConfig.setKey(DESERIALIZATION_JAR);
        desensitizationJarConfig.setDesensitizationJar(dtoFromDao(desensitizationJarCollection));

        Update update = MongoHelper.getUpdate();
        MongoHelper.appendFullProperties(update, desensitizationJarConfig);
        mongoTemplate.upsert(Query.query(Criteria.where(SystemConfigurationCollection.Fields.key).is(DESERIALIZATION_JAR)),
            update, SystemConfigurationCollection.class);
      }

      // set completion position flag
      markTaskFinish(refreshTaskContext);
      LOGGER.info("finish transferSystemConfigTask");
    };

    RefreshTaskContext refreshTaskContext = new RefreshTaskContext(mongoTemplate, RefreshTaskName.TRANSFER_SYSTEM_CONFIG);
    return new LockRefreshTask<>(cacheProvider, redisLeaseTime, task, refreshTaskContext);
  }

  private DesensitizationJar dtoFromDao(DesensitizationJarCollection dao) {
    DesensitizationJar desensitizationJar = new DesensitizationJar();
    if (dao == null) {
      return desensitizationJar;
    }
    desensitizationJar.setJarUrl(dao.getJarUrl());
    desensitizationJar.setRemark(dao.getRemark());
    desensitizationJar.setUploadDate(dao.getUploadDate());
    return desensitizationJar;
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

  private boolean isTaskFinish(RefreshTaskContext refreshTaskContext) {
    Query query = Query.query(
        Criteria.where(SystemConfigurationCollection.Fields.key).is(REFRESH_DATA));
    SystemConfigurationCollection systemConfiguration = refreshTaskContext.getMongoTemplate()
        .findOne(query,
            SystemConfigurationCollection.class);
    return systemConfiguration != null && systemConfiguration.getRefreshTaskMark() != null
        && systemConfiguration.getRefreshTaskMark().containsKey(refreshTaskContext.getTaskName());
  }

  private void markTaskFinish(RefreshTaskContext refreshTaskContext) {
    Query query = Query.query(
        Criteria.where(SystemConfigurationCollection.Fields.key).is(REFRESH_DATA));
    Update update = MongoHelper.getUpdate();
    update.inc(SystemConfigurationCollection.Fields.refreshTaskMark + "."
        + refreshTaskContext.getTaskName(), 1);
    refreshTaskContext.getMongoTemplate()
        .upsert(query, update, SystemConfigurationCollection.class);
  }


  @Data
  @AllArgsConstructor
  private static class LockRefreshTask<T extends RefreshTaskContext> implements Runnable {

    private CacheProvider cacheProvider;

    private long redisLeaseTime;

    private Consumer<T> task;

    private T refreshTaskContext;

    @Override
    public void run() {
      String taskName = refreshTaskContext.getTaskName();
      LockWrapper lock = cacheProvider.getLock(taskName);
      try {
        lock.lock(redisLeaseTime, TimeUnit.SECONDS);
        task.accept(refreshTaskContext);
      } catch (RuntimeException e) {
        LOGGER.error("Failed to clean data, taskName:{}, exception:{}", taskName, e);
      } finally {
        lock.unlock();
      }
    }

  }


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class RefreshTaskContext {

    @NonNull
    private MongoTemplate mongoTemplate;
    @NonNull
    private String taskName;
  }


  private interface RefreshTaskName {

    // to do the method "cleanConfigComparisonIgnoreCategoryCollection"
    String CLEAN_CONFIG_COMPARISON_IGNORE_CATEGORY = "cleanConfigComparisonIgnoreCategory";

    // to do the method "buildAddMissingRecordServiceConfigTask"
    String BUILD_ADD_MISSING_RECORD_SERVICE_CONFIG = "missingRecordConfig";

    // transfer SystemConfig & DesensitizationJar to SystemConfiguration
    String TRANSFER_SYSTEM_CONFIG = "transferSystemConfig";

  }


}
