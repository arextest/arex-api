package com.arextest.report.core.repository.mongo;


import com.arextest.report.core.repository.ConfigRepositoryField;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.api.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.report.model.dao.mongodb.ConfigComparisonInclusionsCollection;
import com.arextest.report.model.mapper.ConfigComparisonInclusionsMapper;
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

@Repository
public class ComparisonInclusionsConfigurationRepositoryImpl implements ConfigRepositoryProvider<ComparisonInclusionsConfiguration>,
        ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String INCLUSIONS = "inclusions";
    private static final String EXPIRATION_TYPE = "expirationType";
    private static final String EXPIRATION_DATE = "expirationDate";


    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonInclusionsConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonInclusionsConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonInclusionsCollection> configComparisonInclusionsCollections = mongoTemplate.find(query, ConfigComparisonInclusionsCollection.class);
        return configComparisonInclusionsCollections.stream().map(ConfigComparisonInclusionsMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonInclusionsConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.assertNull("update parameter is null", configuration.getInclusions(),
                configuration.getExpirationDate());
        update.set(INCLUSIONS, configuration.getInclusions());
        update.set(EXPIRATION_TYPE, configuration.getExpirationType());
        update.set(EXPIRATION_DATE, configuration.getExpirationDate());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ConfigComparisonInclusionsCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ComparisonInclusionsConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonInclusionsCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ComparisonInclusionsConfiguration configuration) {
        ConfigComparisonInclusionsCollection configComparisonInclusionsCollection = ConfigComparisonInclusionsMapper.INSTANCE.daoFromDto(configuration);
        ConfigComparisonInclusionsCollection insert = mongoTemplate.insert(configComparisonInclusionsCollection);
        if (insert.getId() != null) {
            configuration.setId(insert.getId());
        }
        return insert.getId() != null;
    }

    @Override
    public boolean insertList(List<ComparisonInclusionsConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        List<ConfigComparisonInclusionsCollection> configComparisonInclusionsCollections = configurationList.stream()
                .map(ConfigComparisonInclusionsMapper.INSTANCE::daoFromDto)
                .collect(Collectors.toList());
        Collection<ConfigComparisonInclusionsCollection> insertAll = mongoTemplate.insertAll(configComparisonInclusionsCollections);
        return CollectionUtils.isNotEmpty(insertAll);
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonInclusionsCollection.class);
        return remove.getDeletedCount() > 0;
    }
}