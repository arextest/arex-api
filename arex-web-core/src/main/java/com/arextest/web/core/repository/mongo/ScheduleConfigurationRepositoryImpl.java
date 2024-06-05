package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import com.arextest.web.model.dao.mongodb.ReplayScheduleConfigCollection;
import com.arextest.web.model.dao.mongodb.ReplayScheduleConfigCollection.Fields;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.mapper.ReplayScheduleConfigMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ScheduleConfigurationRepositoryImpl implements
    ConfigRepositoryProvider<ScheduleConfiguration> {

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  ObjectMapper objectMapper;

  @Override
  public List<ScheduleConfiguration> list() {
    Query query = new Query();
    List<ReplayScheduleConfigCollection> replayScheduleConfigCollections =
        mongoTemplate.find(query, ReplayScheduleConfigCollection.class);
    return replayScheduleConfigCollections.stream()
        .map(ReplayScheduleConfigMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ScheduleConfiguration> listBy(String appId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
    List<ReplayScheduleConfigCollection> replayScheduleConfigCollections =
        mongoTemplate.find(query, ReplayScheduleConfigCollection.class);
    return replayScheduleConfigCollections.stream()
        .map(ReplayScheduleConfigMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean update(ScheduleConfiguration configuration) {
    Query query = Query.query(
        Criteria.where(AbstractComparisonDetails.Fields.appId).is(configuration.getAppId()));
    Update update = MongoHelper.getConfigUpdate();
    MongoHelper.appendSpecifiedProperties(update, configuration, Fields.targetEnv,
        Fields.sendMaxQps,
        Fields.offsetDays, Fields.mockHandlerJarUrl);
    if (configuration.getExcludeOperationMap() != null) {
      try {
        update.set(Fields.excludeOperationMap,
            objectMapper.writeValueAsString(configuration.getExcludeOperationMap()));
      } catch (JsonProcessingException e) {
        LogUtils.error(LOGGER,
            "ScheduleConfigurationRepositoryImpl.update: serialize excludeOperationMap failed ", e);
      }
    }
    UpdateResult updateResult = mongoTemplate.updateMulti(query, update,
        ReplayScheduleConfigCollection.class);
    return updateResult.getModifiedCount() > 0;
  }

  @Override
  public boolean remove(ScheduleConfiguration configuration) {
    Query query = Query.query(
        Criteria.where(AbstractComparisonDetails.Fields.appId).is(configuration.getAppId()));
    DeleteResult remove = mongoTemplate.remove(query, ReplayScheduleConfigCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public boolean insert(ScheduleConfiguration configuration) {
    ReplayScheduleConfigCollection replayScheduleConfigCollection =
        ReplayScheduleConfigMapper.INSTANCE.daoFromDto(configuration);
    ReplayScheduleConfigCollection insert = mongoTemplate.insert(replayScheduleConfigCollection);
    return insert.getId() != null;
  }

  @Override
  public boolean removeByAppId(String appId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
    DeleteResult remove = mongoTemplate.remove(query, ReplayScheduleConfigCollection.class);
    return remove.getDeletedCount() > 0;
  }
}
