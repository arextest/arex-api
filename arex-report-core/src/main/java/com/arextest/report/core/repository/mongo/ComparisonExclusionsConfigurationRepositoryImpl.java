package com.arextest.report.core.repository.mongo;


import com.arextest.report.core.repository.ConfigRepositoryField;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.api.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.report.model.dao.mongodb.ConfigComparisonExclusionsCollection;
import com.arextest.report.model.mapper.ConfigComparisonExclusionsMapper;
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
public class ComparisonExclusionsConfigurationRepositoryImpl implements ConfigRepositoryProvider<ComparisonExclusionsConfiguration>,
        ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String EXCLUSIONS = "exclusions";
    private static final String EXPIRATION_TYPE = "expirationType";
    private static final String EXPIRATION_DATE = "expirationDate";


    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonExclusionsConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonExclusionsConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections = mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
        return configComparisonExclusionsCollections.stream().map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonExclusionsConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.assertNull("update parameter is null", configuration.getExclusions(),
                configuration.getExpirationDate());
        update.set(EXCLUSIONS, configuration.getExclusions());
        update.set(EXPIRATION_TYPE, configuration.getExpirationType());
        update.set(EXPIRATION_DATE, configuration.getExpirationDate());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ConfigComparisonExclusionsCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ComparisonExclusionsConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonExclusionsCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ComparisonExclusionsConfiguration configuration) {
        ConfigComparisonExclusionsCollection configComparisonExclusionsCollection = ConfigComparisonExclusionsMapper.INSTANCE.daoFromDto(configuration);
        ConfigComparisonExclusionsCollection insert = mongoTemplate.insert(configComparisonExclusionsCollection);
        if (insert.getId() != null) {
            configuration.setId(insert.getId());
        }
        return insert.getId() != null;
    }

    @Override
    public boolean insertList(List<ComparisonExclusionsConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        List<ConfigComparisonExclusionsCollection> comparisonExclusionsConfigurations = configurationList.stream()
                .map(ConfigComparisonExclusionsMapper.INSTANCE::daoFromDto)
                .collect(Collectors.toList());
        Collection<ConfigComparisonExclusionsCollection> insertAll = mongoTemplate.insertAll(comparisonExclusionsConfigurations);
        return CollectionUtils.isNotEmpty(insertAll);
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonExclusionsCollection.class);
        return remove.getDeletedCount() > 0;
    }
}