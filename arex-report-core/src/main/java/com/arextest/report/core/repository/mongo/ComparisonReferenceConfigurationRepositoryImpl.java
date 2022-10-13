package com.arextest.report.core.repository.mongo;


import com.arextest.report.core.repository.ConfigRepositoryField;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.api.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.report.model.dao.mongodb.ConfigComparisonReferenceCollection;
import com.arextest.report.model.mapper.ConfigComparisonReferenceMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Repository
public class ComparisonReferenceConfigurationRepositoryImpl implements ConfigRepositoryProvider<ComparisonReferenceConfiguration>,
        ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String OPERATION_ID = "operationId";
    private static final String PK_PATH = "pkPath";
    private static final String FK_PATH = "fkPath";
    private static final String EXPIRATION_TYPE = "expirationType";
    private static final String EXPIRATION_DATE = "expirationDate";


    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonReferenceConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonReferenceConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonReferenceCollection> configComparisonReferenceCollections = mongoTemplate.find(query, ConfigComparisonReferenceCollection.class);
        return configComparisonReferenceCollections.stream().map(ConfigComparisonReferenceMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    public List<ComparisonReferenceConfiguration> listBy(String appId, String operationId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId).and(OPERATION_ID).is(operationId));
        List<ConfigComparisonReferenceCollection> configComparisonReferenceCollections = mongoTemplate.find(query, ConfigComparisonReferenceCollection.class);
        return configComparisonReferenceCollections.stream().map(ConfigComparisonReferenceMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonReferenceConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration, PK_PATH, FK_PATH, EXPIRATION_TYPE, EXPIRATION_DATE);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ConfigComparisonReferenceCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ComparisonReferenceConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonReferenceCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ComparisonReferenceConfiguration configuration) {
        ConfigComparisonReferenceCollection configComparisonReferenceCollection = ConfigComparisonReferenceMapper.INSTANCE.daoFromDto(configuration);
        ConfigComparisonReferenceCollection insert = mongoTemplate.insert(configComparisonReferenceCollection);
        if (insert.getId() != null) {
            configuration.setId(insert.getId());
        }
        return insert.getId() != null;
    }

    @Override
    public boolean insertList(List<ComparisonReferenceConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        List<ConfigComparisonReferenceCollection> configComparisonReferenceCollections = configurationList.stream()
                .map(ConfigComparisonReferenceMapper.INSTANCE::daoFromDto)
                .collect(Collectors.toList());
        Collection<ConfigComparisonReferenceCollection> insertAll = mongoTemplate.insertAll(configComparisonReferenceCollections);
        return CollectionUtils.isNotEmpty(insertAll);
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonReferenceCollection.class);
        return remove.getDeletedCount() > 0;
    }
}