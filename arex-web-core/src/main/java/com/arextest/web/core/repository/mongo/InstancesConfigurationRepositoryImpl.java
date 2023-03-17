package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import com.arextest.web.model.dao.mongodb.InstancesCollection;
import com.arextest.web.model.mapper.InstancesMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InstancesConfigurationRepositoryImpl implements ConfigRepositoryProvider<InstancesConfiguration>,
        ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String RECORD_VERSION = "recordVersion";
    private static final String HOST = "host";

    private static final String DATA_CHANGE_UPDATE_TIME = "dataChangeUpdateTime";
    private static final String DATA_UPDATE_TIME = "dataUpdateTime";

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
        query.with(Sort.by(Sort.Order.desc(DATA_CHANGE_UPDATE_TIME)));
        List<InstancesCollection> instancesCollections = mongoTemplate.find(query, InstancesCollection.class);
        return instancesCollections.stream().map(InstancesMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(InstancesConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        update.set(RECORD_VERSION, configuration.getRecordVersion());
        update.set(HOST, configuration.getHost());
        update.set(DATA_UPDATE_TIME, configuration.getDataUpdateTime());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, InstancesCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(InstancesConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, InstancesCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(InstancesConfiguration configuration) {
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
