package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.RepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import com.arextest.web.model.dao.mongodb.InstancesCollection;
import com.arextest.web.model.mapper.InstancesMapper;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InstancesConfigurationRepositoryImpl implements ConfigRepositoryProvider<InstancesConfiguration>,
        ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String HOST = "host";

    private static final String DATA_UPDATE_TIME = "dataUpdateTime";
    private static final String STATUS = "status";
    private static final Integer WORKING = 1;


    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<InstancesConfiguration> list() {
        Query query = new Query();
        List<InstancesCollection> instancesCollections = mongoTemplate.find(query, InstancesCollection.class);
        return instancesCollections.stream().map(InstancesMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public List<InstancesConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<InstancesCollection> instancesCollections = mongoTemplate.find(query, InstancesCollection.class);
        return instancesCollections.stream().map(InstancesMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    public List<InstancesConfiguration> listBy(String appId, int top) {
        if (top == 0) {
            return Collections.emptyList();
        }
        Query query = Query.query(Criteria.where(APP_ID).is(appId)).limit(top).with(Sort.by(DASH_ID).ascending());
        List<InstancesCollection> instancesCollections = mongoTemplate.find(query, InstancesCollection.class);
        return instancesCollections.stream().map(InstancesMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    public List<InstancesConfiguration> listOfWorking() {
        Query query = Query.query(Criteria.where(STATUS).is(WORKING));
        List<InstancesCollection> instancesCollections = mongoTemplate.find(query, InstancesCollection.class);
        return instancesCollections.stream().map(InstancesMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(InstancesConfiguration configuration) {
        Query query =
                Query.query(Criteria.where(APP_ID).is(configuration.getAppId()).and(HOST).is(configuration.getHost()));
        Update update = MongoHelper.getConfigUpdate();
        InstancesCollection dao = InstancesMapper.INSTANCE.daoFromDto(configuration);
        MongoHelper.appendFullProperties(update, dao);
        update.setOnInsert(RepositoryProvider.DATA_CHANGE_CREATE_TIME, System.currentTimeMillis());
        update.set(DATA_UPDATE_TIME, new Date());
        try {
            mongoTemplate.findAndModify(query,
                    update,
                    FindAndModifyOptions.options().returnNew(true).upsert(true),
                    InstancesCollection.class);
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "update instances error", e);
            return false;
        }
    }

    @Override
    public boolean remove(InstancesConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, InstancesCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(InstancesConfiguration configuration) {
        configuration.setDataUpdateTime(new Date());
        InstancesCollection instancesCollection = InstancesMapper.INSTANCE.daoFromDto(configuration);
        InstancesCollection insert = mongoTemplate.insert(instancesCollection);
        if (insert.getId() != null) {
            configuration.setId(insert.getId());
        }
        return insert.getId() != null;
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, InstancesCollection.class);
        return remove.getDeletedCount() > 0;
    }

}
