package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonListSortCollection;
import com.arextest.web.model.mapper.ConfigComparisonListSortMapper;
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
public class ComparisonListSortConfigurationRepositoryImpl implements
        ConfigRepositoryProvider<ComparisonListSortConfiguration>,
        ConfigRepositoryField {

    private static final String APP_ID = "appId";
    private static final String OPERATION_ID = "operationId";
    private static final String LIST_PATH = "listPath";
    private static final String KEYS = "keys";
    private static final String EXPIRATION_TYPE = "expirationType";
    private static final String EXPIRATION_DATE = "expirationDate";
    private static final String FS_INTERFACE_ID = "fsInterfaceId";


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

    public List<ComparisonListSortConfiguration> listBy(String appId, String operationId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId).and(OPERATION_ID).is(operationId));
        List<ConfigComparisonListSortCollection> configComparisonListSortCollections = mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
        return configComparisonListSortCollections.stream().map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public List<ComparisonListSortConfiguration> queryByInterfaceIdAndOperationId(String interfaceId,
                                                                                  String operationId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(operationId)) {
            query.addCriteria(new Criteria().orOperator(Criteria.where(FS_INTERFACE_ID).is(interfaceId),
                    Criteria.where(OPERATION_ID).is(operationId)));
        } else {
            query.addCriteria(Criteria.where(FS_INTERFACE_ID).is(interfaceId));
        }
        List<ConfigComparisonListSortCollection> configComparisonListSortCollections =
                mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
        return configComparisonListSortCollections.stream()
                .map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonListSortConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration, LIST_PATH, KEYS, EXPIRATION_TYPE, EXPIRATION_DATE);
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
        ConfigComparisonListSortCollection configComparisonListSortCollection =
                ConfigComparisonListSortMapper.INSTANCE.daoFromDto(configuration);

        Update update = new Update();
        MongoHelper.appendFullProperties(update, configComparisonListSortCollection);

        Query query = Query.query(
                Criteria.where(APP_ID).is(configComparisonListSortCollection.getAppId())
                        .and(OPERATION_ID).is(configComparisonListSortCollection.getOperationId())
                        .and(EXPIRATION_TYPE).is(configComparisonListSortCollection.getExpirationType())
                        .and(FS_INTERFACE_ID).is(configComparisonListSortCollection.getFsInterfaceId())
                        .and(LIST_PATH).is(configComparisonListSortCollection.getListPath())
                        .and(KEYS).is(configComparisonListSortCollection.getKeys())
        );

        ComparisonListSortConfiguration dao = mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                ComparisonListSortConfiguration.class);
        return dao != null;
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