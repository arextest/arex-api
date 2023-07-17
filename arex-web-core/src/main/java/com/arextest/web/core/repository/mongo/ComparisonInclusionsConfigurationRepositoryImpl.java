package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonExclusionsCollection;
import com.arextest.web.model.dao.mongodb.ConfigComparisonInclusionsCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.mapper.ConfigComparisonExclusionsMapper;
import com.arextest.web.model.mapper.ConfigComparisonInclusionsMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ComparisonInclusionsConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonInclusionsConfiguration>, ConfigRepositoryField {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonInclusionsConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonInclusionsConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonInclusionsCollection> configComparisonInclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonInclusionsCollection.class);
        return configComparisonInclusionsCollections.stream().map(ConfigComparisonInclusionsMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    public List<ComparisonInclusionsConfiguration> listBy(String appId, String operationId) {
        Query query = Query
            .query(Criteria.where(APP_ID).is(appId).and(AbstractComparisonDetails.Fields.operationId).is(operationId));
        List<ConfigComparisonInclusionsCollection> configComparisonInclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonInclusionsCollection.class);
        return configComparisonInclusionsCollections.stream().map(ConfigComparisonInclusionsMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonInclusionsConfiguration> queryByInterfaceIdAndOperationId(String interfaceId,
        String operationId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(operationId)) {
            query.addCriteria(new Criteria().orOperator(
                Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId),
                Criteria.where(AbstractComparisonDetails.Fields.operationId).is(operationId)));
        } else {
            query.addCriteria(Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId));
        }
        List<ConfigComparisonInclusionsCollection> comparisonInclusionsConfigurations =
            mongoTemplate.find(query, ConfigComparisonInclusionsCollection.class);
        return comparisonInclusionsConfigurations.stream().map(ConfigComparisonInclusionsMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonInclusionsConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration,
            ConfigComparisonInclusionsCollection.Fields.inclusions, AbstractComparisonDetails.Fields.expirationType,
            AbstractComparisonDetails.Fields.expirationDate);
        UpdateResult updateResult =
            mongoTemplate.updateMulti(query, update, ConfigComparisonInclusionsCollection.class);
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
        ConfigComparisonInclusionsCollection configComparisonInclusionsCollection =
            ConfigComparisonInclusionsMapper.INSTANCE.daoFromDto(configuration);

        Update update = new Update();
        MongoHelper.appendFullProperties(update, configComparisonInclusionsCollection);

        Query query = Query.query(Criteria.where(APP_ID).is(configComparisonInclusionsCollection.getAppId())
            .and(AbstractComparisonDetails.Fields.operationId).is(configComparisonInclusionsCollection.getOperationId())
            .and(AbstractComparisonDetails.Fields.compareConfigType)
            .is(configComparisonInclusionsCollection.getCompareConfigType())
            .and(AbstractComparisonDetails.Fields.fsInterfaceId)
            .is(configComparisonInclusionsCollection.getFsInterfaceId())
            .and(AbstractComparisonDetails.Fields.operationType).is(configuration.getOperationType())
            .and(AbstractComparisonDetails.Fields.operationName).is(configuration.getOperationName())
            .and(ConfigComparisonInclusionsCollection.Fields.inclusions)
            .is(configComparisonInclusionsCollection.getInclusions()));

        ConfigComparisonInclusionsCollection dao = mongoTemplate.findAndModify(query, update,
            FindAndModifyOptions.options().returnNew(true).upsert(true), ConfigComparisonInclusionsCollection.class);
        return dao != null;
    }

    @Override
    public boolean insertList(List<ComparisonInclusionsConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)) {
            return false;
        }
        List<ConfigComparisonInclusionsCollection> inclusionsCollections = configurationList.stream()
            .map(ConfigComparisonInclusionsMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
        try {
            BulkOperations bulkOperations =
                mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ConfigComparisonInclusionsCollection.class);
            for (ConfigComparisonInclusionsCollection inclusionsCollection : inclusionsCollections) {
                Update update = new Update();
                MongoHelper.appendFullProperties(update, inclusionsCollection);

                Query query = Query.query(Criteria.where(APP_ID).is(inclusionsCollection.getAppId())
                    .and(AbstractComparisonDetails.Fields.operationId).is(inclusionsCollection.getOperationId())
                    .and(AbstractComparisonDetails.Fields.compareConfigType)
                    .is(inclusionsCollection.getCompareConfigType()).and(AbstractComparisonDetails.Fields.fsInterfaceId)
                    .is(inclusionsCollection.getFsInterfaceId()).and(AbstractComparisonDetails.Fields.operationType)
                    .is(inclusionsCollection.getOperationType()).and(AbstractComparisonDetails.Fields.operationName)
                    .is(inclusionsCollection.getOperationName())
                    .and(ConfigComparisonInclusionsCollection.Fields.inclusions)
                    .is(inclusionsCollection.getInclusions()));
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
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonInclusionsCollection.class);
        return remove.getDeletedCount() > 0;
    }
}