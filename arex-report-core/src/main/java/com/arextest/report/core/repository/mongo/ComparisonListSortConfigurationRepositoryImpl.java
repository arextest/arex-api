package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.ConfigRepositoryField;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.api.contracts.configservice.replay.ComparisonListSortConfiguration;
import com.arextest.report.model.dao.mongodb.ConfigComparisonExclusionsCollection;
import com.arextest.report.model.dao.mongodb.ConfigComparisonListSortCollection;
import com.arextest.report.model.mapper.ConfigComparisonListSortMapper;
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
public class ComparisonListSortConfigurationRepositoryImpl implements ConfigRepositoryProvider<ComparisonListSortConfiguration>,
        ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String LIST_PATH = "listPath";
    private static final String KEYS = "keys";
    private static final String EXPIRATION_TYPE = "expirationType";
    private static final String EXPIRATION_DATE = "expirationDate";


    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonListSortConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonListSortConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonListSortCollection> configComparisonListSortCollections = mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
        return configComparisonListSortCollections.stream().map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonListSortConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.assertNull("update parameter is null", configuration.getListPath(), configuration.getKeys(),
                configuration.getExpirationDate());
        update.set(LIST_PATH, configuration.getListPath());
        update.set(KEYS, configuration.getKeys());
        update.set(EXPIRATION_TYPE, configuration.getExpirationType());
        update.set(EXPIRATION_DATE, configuration.getExpirationDate());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ConfigComparisonListSortCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ComparisonListSortConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonListSortCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ComparisonListSortConfiguration configuration) {
        ConfigComparisonListSortCollection configComparisonListSortCollection = ConfigComparisonListSortMapper.INSTANCE.daoFromDto(configuration);
        ConfigComparisonListSortCollection insert = mongoTemplate.insert(configComparisonListSortCollection);
        if (insert.getId() != null) {
            configuration.setId(insert.getId());
        }
        return insert.getId() != null;
    }

    @Override
    public boolean insertList(List<ComparisonListSortConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        List<ConfigComparisonListSortCollection> configComparisonListSortCollections = configurationList.stream()
                .map(ConfigComparisonListSortMapper.INSTANCE::daoFromDto)
                .collect(Collectors.toList());
        Collection<ConfigComparisonListSortCollection> insertAll = mongoTemplate.insertAll(configComparisonListSortCollections);
        return CollectionUtils.isNotEmpty(insertAll);
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonListSortCollection.class);
        return remove.getDeletedCount() > 0;
    }
}