package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.ConfigRepositoryField;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.api.contracts.config.record.DynamicClassConfiguration;
import com.arextest.report.model.dao.mongodb.DynamicClassCollection;
import com.arextest.report.model.mapper.DynamicClassMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DynamicClassConfigurationRepositoryImpl implements ConfigRepositoryProvider<DynamicClassConfiguration>, ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String FULL_CLASS_NAME = "fullClassName";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<DynamicClassConfiguration> list() {
        Query query = new Query();
        List<DynamicClassCollection> dynamicClassCollections = mongoTemplate.find(query, DynamicClassCollection.class);
        return dynamicClassCollections.stream().map(DynamicClassMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public List<DynamicClassConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<DynamicClassCollection> dynamicClassCollections = mongoTemplate.find(query, DynamicClassCollection.class);
        return dynamicClassCollections.stream().map(DynamicClassMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(DynamicClassConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        update.set(FULL_CLASS_NAME, configuration.getFullClassName());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, DynamicClassCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(DynamicClassConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, DynamicClassCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(DynamicClassConfiguration configuration) {
        DynamicClassCollection dynamicClassConfiguration = DynamicClassMapper.INSTANCE.daoFromDto(configuration);
        DynamicClassCollection insert = mongoTemplate.insert(dynamicClassConfiguration);
        if (insert.getId() != null) {
            configuration.setId(insert.getId());
        }
        return insert.getId() != null;
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, DynamicClassCollection.class);
        return remove.getDeletedCount() > 0;
    }
}
