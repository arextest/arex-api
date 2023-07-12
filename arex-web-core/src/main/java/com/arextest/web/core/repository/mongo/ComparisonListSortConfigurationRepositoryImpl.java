package com.arextest.web.core.repository.mongo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.arextest.web.common.LogUtils;
import com.arextest.web.model.dao.mongodb.ConfigComparisonInclusionsCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonListSortCollection;
import com.arextest.web.model.mapper.ConfigComparisonListSortMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Slf4j
@Repository
public class ComparisonListSortConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonListSortConfiguration>, ConfigRepositoryField {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonListSortConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonListSortConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonListSortCollection> configComparisonListSortCollections =
            mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
        return configComparisonListSortCollections.stream().map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    public List<ComparisonListSortConfiguration> listBy(String appId, String operationId) {
        Query query = Query
            .query(Criteria.where(APP_ID).is(appId).and(AbstractComparisonDetails.Fields.operationId).is(operationId));
        List<ConfigComparisonListSortCollection> configComparisonListSortCollections =
            mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
        return configComparisonListSortCollections.stream().map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonListSortConfiguration> queryByInterfaceIdAndOperationId(String interfaceId,
        String operationId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(operationId)) {
            query.addCriteria(new Criteria().orOperator(
                Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId),
                Criteria.where(AbstractComparisonDetails.Fields.operationId).is(operationId)));
        } else {
            query.addCriteria(Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId));
        }
        List<ConfigComparisonListSortCollection> configComparisonListSortCollections =
            mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
        return configComparisonListSortCollections.stream().map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonListSortConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration, ConfigComparisonListSortCollection.Fields.listPath,
            ConfigComparisonListSortCollection.Fields.keys, AbstractComparisonDetails.Fields.expirationType,
            AbstractComparisonDetails.Fields.expirationDate);
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

        Query query = Query.query(Criteria.where(APP_ID).is(configComparisonListSortCollection.getAppId())
            .and(AbstractComparisonDetails.Fields.operationId).is(configComparisonListSortCollection.getOperationId())
            .and(AbstractComparisonDetails.Fields.compareConfigType)
            .is(configComparisonListSortCollection.getCompareConfigType())
            .and(AbstractComparisonDetails.Fields.fsInterfaceId)
            .is(configComparisonListSortCollection.getFsInterfaceId())
            .and(AbstractComparisonDetails.Fields.dependencyId).is(configComparisonListSortCollection.getDependencyId())
            .and(ConfigComparisonListSortCollection.Fields.listPath)
            .is(configComparisonListSortCollection.getListPath()));

        ConfigComparisonListSortCollection dao = mongoTemplate.findAndModify(query, update,
            FindAndModifyOptions.options().returnNew(true).upsert(true), ConfigComparisonListSortCollection.class);
        return dao != null;
    }

    @Override
    public boolean insertList(List<ComparisonListSortConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        List<ConfigComparisonListSortCollection> listSortCollections = configurationList.stream()
            .map(ConfigComparisonListSortMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
        try {
            BulkOperations bulkOperations =
                mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ConfigComparisonListSortCollection.class);
            for (ConfigComparisonListSortCollection listSortCollection : listSortCollections) {
                Update update = new Update();
                MongoHelper.appendFullProperties(update, listSortCollection);

                Query query = Query.query(Criteria.where(APP_ID).is(listSortCollection.getAppId())
                    .and(AbstractComparisonDetails.Fields.operationId).is(listSortCollection.getOperationId())
                    .and(AbstractComparisonDetails.Fields.compareConfigType)
                    .is(listSortCollection.getCompareConfigType()).and(AbstractComparisonDetails.Fields.fsInterfaceId)
                    .is(listSortCollection.getFsInterfaceId()).and(AbstractComparisonDetails.Fields.dependencyId)
                    .is(listSortCollection.getDependencyId()).and(ConfigComparisonListSortCollection.Fields.listPath)
                    .is(listSortCollection.getListPath()));
                bulkOperations.upsert(query, update);
            }
            bulkOperations.execute();
        } catch (Exception e) {
            LogUtils.error(LOGGER, "exclusion insertList failed! list:{}, exception:{}", configurationList, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonListSortCollection.class);
        return remove.getDeletedCount() > 0;
    }
}