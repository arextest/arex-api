package com.arextest.web.core.repository.mongo;


import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonExclusionsCollection;
import com.arextest.web.model.mapper.ConfigComparisonExclusionsMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
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
public class ComparisonExclusionsConfigurationRepositoryImpl implements
        ConfigRepositoryProvider<ComparisonExclusionsConfiguration>,
        ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String OPERATION_ID = "operationId";
    private static final String EXCLUSIONS = "exclusions";
    private static final String EXPIRATION_TYPE = "expirationType";
    private static final String EXPIRATION_DATE = "expirationDate";
    private static final String FS_INTERFACE_ID = "fsInterfaceId";


    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonExclusionsConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonExclusionsConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections =
                mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
        return configComparisonExclusionsCollections.stream()
                .map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonExclusionsConfiguration> listBy(String appId, String operationId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId).and(OPERATION_ID).is(operationId));
        List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections =
                mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
        return configComparisonExclusionsCollections.stream()
                .map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonExclusionsConfiguration> queryByInterfaceIdAndOperationId(String interfaceId,
                                                                                    String operationId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(operationId)) {
            query.addCriteria(new Criteria().orOperator(Criteria.where(FS_INTERFACE_ID).is(interfaceId),
                    Criteria.where(OPERATION_ID).is(operationId)));
        } else {
            query.addCriteria(Criteria.where(FS_INTERFACE_ID).is(interfaceId));
        }
        List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections =
                mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
        return configComparisonExclusionsCollections.stream()
                .map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonExclusionsConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration, EXCLUSIONS, EXPIRATION_TYPE, EXPIRATION_DATE);
        UpdateResult updateResult =
                mongoTemplate.updateMulti(query, update, ConfigComparisonExclusionsCollection.class);
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
        ConfigComparisonExclusionsCollection configComparisonExclusionsCollection =
                ConfigComparisonExclusionsMapper.INSTANCE.daoFromDto(configuration);

        Update update = new Update();
        MongoHelper.appendFullProperties(update, configComparisonExclusionsCollection);

        Query query = Query.query(
                Criteria.where(APP_ID).is(configComparisonExclusionsCollection.getAppId())
                        .and(OPERATION_ID).is(configComparisonExclusionsCollection.getOperationId())
                        .and(EXPIRATION_TYPE).is(configComparisonExclusionsCollection.getExpirationType())
                        .and(FS_INTERFACE_ID).is(configComparisonExclusionsCollection.getFsInterfaceId())
                        .and(EXCLUSIONS).is(configComparisonExclusionsCollection.getExclusions())
        );

        ConfigComparisonExclusionsCollection dao = mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                ConfigComparisonExclusionsCollection.class);
        return dao != null;
    }

    @Override
    public boolean insertList(List<ComparisonExclusionsConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        List<ConfigComparisonExclusionsCollection> comparisonExclusionsConfigurations = configurationList.stream()
                .map(ConfigComparisonExclusionsMapper.INSTANCE::daoFromDto)
                .collect(Collectors.toList());
        Collection<ConfigComparisonExclusionsCollection> insertAll =
                mongoTemplate.insertAll(comparisonExclusionsConfigurations);
        return CollectionUtils.isNotEmpty(insertAll);
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonExclusionsCollection.class);
        return remove.getDeletedCount() > 0;
    }
}