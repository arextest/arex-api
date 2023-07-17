package com.arextest.web.core.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;

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

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonExclusionsCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.mapper.ConfigComparisonExclusionsMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Slf4j
@Repository
public class ComparisonExclusionsConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonExclusionsConfiguration>, ConfigRepositoryField {

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
        return configComparisonExclusionsCollections.stream().map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonExclusionsConfiguration> listBy(String appId, String operationId) {
        Query query = Query
            .query(Criteria.where(APP_ID).is(appId).and(AbstractComparisonDetails.Fields.operationId).is(operationId));
        List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
        return configComparisonExclusionsCollections.stream().map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonExclusionsConfiguration> queryByInterfaceIdAndOperationId(String interfaceId,
        String operationId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(operationId)) {
            query.addCriteria(new Criteria().orOperator(
                Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId),
                Criteria.where(AbstractComparisonDetails.Fields.operationId).is(operationId)));
        } else {
            query.addCriteria(Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId));
        }
        List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
        return configComparisonExclusionsCollections.stream().map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonExclusionsConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration,
            ConfigComparisonExclusionsCollection.Fields.exclusions, AbstractComparisonDetails.Fields.expirationType,
            AbstractComparisonDetails.Fields.expirationDate);
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

        Query query = Query.query(Criteria.where(APP_ID).is(configComparisonExclusionsCollection.getAppId())
            .and(AbstractComparisonDetails.Fields.operationId).is(configComparisonExclusionsCollection.getOperationId())
            .and(AbstractComparisonDetails.Fields.compareConfigType)
            .is(configComparisonExclusionsCollection.getCompareConfigType())
            .and(AbstractComparisonDetails.Fields.fsInterfaceId)
            .is(configComparisonExclusionsCollection.getFsInterfaceId())
            .and(AbstractComparisonDetails.Fields.dependencyId)
            .is(configComparisonExclusionsCollection.getDependencyId())
            .and(ConfigComparisonExclusionsCollection.Fields.exclusions)
            .is(configComparisonExclusionsCollection.getExclusions()));

        ConfigComparisonExclusionsCollection dao = mongoTemplate.findAndModify(query, update,
            FindAndModifyOptions.options().returnNew(true).upsert(true), ConfigComparisonExclusionsCollection.class);
        return dao != null;
    }

    @Override
    public boolean insertList(List<ComparisonExclusionsConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        List<ConfigComparisonExclusionsCollection> exclusionsCollections = configurationList.stream()
            .map(ConfigComparisonExclusionsMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
        try {
            BulkOperations bulkOperations =
                mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ConfigComparisonExclusionsCollection.class);
            for (ConfigComparisonExclusionsCollection exclusionsCollection : exclusionsCollections) {
                Update update = new Update();
                MongoHelper.appendFullProperties(update, exclusionsCollection);

                Query query = Query.query(Criteria.where(APP_ID).is(exclusionsCollection.getAppId())
                    .and(AbstractComparisonDetails.Fields.operationId).is(exclusionsCollection.getOperationId())
                    .and(AbstractComparisonDetails.Fields.compareConfigType)
                    .is(exclusionsCollection.getCompareConfigType()).and(AbstractComparisonDetails.Fields.fsInterfaceId)
                    .is(exclusionsCollection.getFsInterfaceId()).and(AbstractComparisonDetails.Fields.dependencyId)
                    .is(exclusionsCollection.getDependencyId())
                    .and(ConfigComparisonExclusionsCollection.Fields.exclusions)
                    .is(exclusionsCollection.getExclusions()));
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
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonExclusionsCollection.class);
        return remove.getDeletedCount() > 0;
    }
}