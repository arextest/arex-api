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

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonInclusionsCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.mapper.ConfigComparisonInclusionsMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ComparisonInclusionsConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonInclusionsConfiguration> {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonInclusionsConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonInclusionsConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
        List<ConfigComparisonInclusionsCollection> configComparisonInclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonInclusionsCollection.class);
        return configComparisonInclusionsCollections.stream().map(ConfigComparisonInclusionsMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    public List<ComparisonInclusionsConfiguration> listBy(String appId, String operationId) {
        Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId)
            .and(AbstractComparisonDetails.Fields.operationId).is(operationId));
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
            Criteria fsInterfaceConfigQuery =
                Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId);
            Criteria operationConfigQuery = Criteria.where(AbstractComparisonDetails.Fields.operationId).is(operationId)
                .andOperator(Criteria.where(AbstractComparisonDetails.Fields.dependencyId).is(null));
            query.addCriteria(new Criteria().orOperator(fsInterfaceConfigQuery, operationConfigQuery));
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

        Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
            .is(configComparisonInclusionsCollection.getAppId()).and(AbstractComparisonDetails.Fields.operationId)
            .is(configComparisonInclusionsCollection.getOperationId())
            .and(AbstractComparisonDetails.Fields.compareConfigType)
            .is(configComparisonInclusionsCollection.getCompareConfigType())
            .and(AbstractComparisonDetails.Fields.fsInterfaceId)
            .is(configComparisonInclusionsCollection.getFsInterfaceId())
            .and(AbstractComparisonDetails.Fields.dependencyId)
            .is(configComparisonInclusionsCollection.getDependencyId())
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

                Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
                    .is(inclusionsCollection.getAppId()).and(AbstractComparisonDetails.Fields.operationId)
                    .is(inclusionsCollection.getOperationId()).and(AbstractComparisonDetails.Fields.compareConfigType)
                    .is(inclusionsCollection.getCompareConfigType()).and(AbstractComparisonDetails.Fields.fsInterfaceId)
                    .is(inclusionsCollection.getFsInterfaceId()).and(AbstractComparisonDetails.Fields.dependencyId)
                    .is(inclusionsCollection.getDependencyId())
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
        Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonInclusionsCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public ComparisonInclusionsConfiguration queryById(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        ConfigComparisonInclusionsCollection dao =
            mongoTemplate.findOne(query, ConfigComparisonInclusionsCollection.class);
        return ConfigComparisonInclusionsMapper.INSTANCE.dtoFromDao(dao);
    }
}