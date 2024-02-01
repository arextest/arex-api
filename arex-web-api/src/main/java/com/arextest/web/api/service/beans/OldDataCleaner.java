package com.arextest.web.api.service.beans;

import com.arextest.common.cache.CacheProvider;
import com.arextest.common.cache.LockWrapper;
import com.arextest.config.model.dao.config.AppCollection;
import com.arextest.config.model.dao.config.RecordServiceConfigCollection;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.ConfigComparisonIgnoreCategoryCollection;
import com.arextest.web.model.dao.mongodb.ModelBase;
import com.arextest.web.model.dao.mongodb.entity.CategoryDetailDao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Data
@Slf4j
@AllArgsConstructor
public class OldDataCleaner implements InitializingBean {

  private CacheProvider cacheProvider;
  private MongoTemplate mongoTemplate;
  private long redisLeaseTime;
  private static final String REFRESH_DATA = "refresh_data";

  @Override
  public void afterPropertiesSet() {
    CompletableFuture.runAsync(cleanConfigComparisonIgnoreCategoryCollection());
    CompletableFuture.runAsync(buildAddMissingRecordServiceConfigTask());
  }

  // Collection ConfigComparisonIgnoreCategory's structure has been changed, need to transfer old data to new.
  // New data was introduced at 0.6.0.17, this method was introduced at 0.6.0.20
  private Runnable cleanConfigComparisonIgnoreCategoryCollection() {
    String taskFinishKey = "ConfigComparisonIgnoreCategory";

    Consumer<RefreshTaskContext> task = (RefreshTaskContext refreshTaskContext) -> {
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
      LOGGER.info("finish clean data for ConfigComparisonIgnoreCategoryCollection");
    };

    RefreshTaskContext refreshTaskContext = new RefreshTaskContext();
    refreshTaskContext.setMongoTemplate(mongoTemplate);

    return new LockRefreshTask<>(taskFinishKey,
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

    String taskFinishKey = "missingRecordConfig";

    Consumer<RefreshTaskContext> task = (RefreshTaskContext refreshTaskContext) -> {

      MongoTemplate taskContextMongoTemplate = refreshTaskContext.getMongoTemplate();
      Query query = Query.query(Criteria.where(MongoKVCollection.Fields.key).is(REFRESH_DATA));
      MongoKVCollection one = taskContextMongoTemplate.findOne(query, MongoKVCollection.class);
      if (one != null && one.mapValue != null && one.mapValue.containsKey(taskFinishKey)) {
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
      Update update = MongoHelper.getUpdate();
      update.inc(MongoKVCollection.Fields.mapValue + "." + taskFinishKey, 1);
      taskContextMongoTemplate.upsert(query, update,
          MongoKVCollection.class);
      LOGGER.info("finish addMissingRecordServiceConfigTask");
    };

    RefreshTaskContext refreshTaskContext = new RefreshTaskContext();
    refreshTaskContext.setMongoTemplate(mongoTemplate);

    return new LockRefreshTask<>(taskFinishKey,
        cacheProvider, redisLeaseTime, task,
        refreshTaskContext);
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


  @Data
  @AllArgsConstructor
  private static class LockRefreshTask<T> implements Runnable {

    @NonNull
    private String taskName;

    private CacheProvider cacheProvider;

    private long redisLeaseTime;

    private Consumer<T> task;

    private T refreshTaskContext;

    @Override
    public void run() {

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
  private static class RefreshTaskContext {

    @NonNull
    private MongoTemplate mongoTemplate;
  }


  @Data
  @FieldNameConstants
  @org.springframework.data.mongodb.core.mapping.Document(collection = "MongoKVCollection")
  private static class MongoKVCollection extends ModelBase {

    /**
     * The problem of prohibiting concurrent repeated insertions, the key is unique
     */
    @Indexed(unique = true)
    private String key;
    private Map<String, Object> mapValue;
  }


}
