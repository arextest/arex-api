package com.arextest.web.core.repository.mongo;


import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import com.arextest.web.model.dao.mongodb.RecordServiceConfigCollection;
import com.arextest.web.model.dao.mongodb.ReplayScheduleConfigCollection;
import com.arextest.web.model.mapper.ReplayScheduleConfigMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ScheduleConfigurationRepositoryImpl implements ConfigRepositoryProvider<ScheduleConfiguration>,
    ConfigRepositoryField {

    private static final String TARGET_ENV = "targetEnv";
    private static final String SEND_MAX_QPS = "sendMaxQps";
    private static final String OFFSET_DAYS = "offsetDays";
    private static final String EXCLUSION_OPERATION_MAP = "excludeOperationMap";

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
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ReplayScheduleConfigCollection> replayScheduleConfigCollections =
            mongoTemplate.find(query, ReplayScheduleConfigCollection.class);
        List<RecordServiceConfigCollection> recordServiceConfigCollections =
            mongoTemplate.find(query, RecordServiceConfigCollection.class);
        Set<String> excludeServiceOperationSet = new HashSet<>();
        if (!recordServiceConfigCollections.isEmpty()) {
            excludeServiceOperationSet = recordServiceConfigCollections.get(0).getExcludeServiceOperationSet();
        }
        List<ScheduleConfiguration> scheduleConfigurationList = replayScheduleConfigCollections.stream()
            .map(ReplayScheduleConfigMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
        Set<String> finalExcludeServiceOperationSet = excludeServiceOperationSet;
        scheduleConfigurationList.forEach(scheduleConfiguration -> {
            scheduleConfiguration.setExcludeServiceOperationSet(finalExcludeServiceOperationSet);
        });
        return scheduleConfigurationList;
    }

    @Override
    public boolean update(ScheduleConfiguration configuration) {
        Query query = Query.query(Criteria.where(APP_ID).is(configuration.getAppId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration, TARGET_ENV, SEND_MAX_QPS, OFFSET_DAYS);
        if (configuration.getExcludeOperationMap() != null) {
            try {
                update.set(EXCLUSION_OPERATION_MAP,
                    objectMapper.writeValueAsString(configuration.getExcludeOperationMap()));
            } catch (JsonProcessingException e) {
                LogUtils.error(LOGGER,
                    "ScheduleConfigurationRepositoryImpl.update: serialize excludeOperationMap failed ",
                    e);
            }
        }
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ReplayScheduleConfigCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ScheduleConfiguration configuration) {
        Query query = Query.query(Criteria.where(APP_ID).is(configuration.getAppId()));
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
}
