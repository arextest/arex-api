package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonEncryptionConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonEncryptionCollection;
import com.arextest.web.model.dao.mongodb.ConfigComparisonExclusionsCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.mapper.ConfigComparisonEncryptionMapper;
import com.arextest.web.model.mapper.ConfigComparisonExclusionsMapper;
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
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ComparisonEncryptionConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonEncryptionConfiguration>, ConfigRepositoryField {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonEncryptionConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonEncryptionConfiguration> listBy(String appId) {
        Query query= Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonEncryptionCollection> configComparisonEncryptionCollections =
                mongoTemplate.find(query, ConfigComparisonEncryptionCollection.class);
        return configComparisonEncryptionCollections.stream().map(ConfigComparisonEncryptionMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonEncryptionConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration, ConfigComparisonEncryptionCollection.Fields.path,
                AbstractComparisonDetails.Fields.expirationType,
                AbstractComparisonDetails.Fields.expirationDate);
        UpdateResult updateResult =
                mongoTemplate.updateMulti(query, update, ConfigComparisonEncryptionCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ComparisonEncryptionConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonEncryptionCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ComparisonEncryptionConfiguration configuration) {
        ConfigComparisonEncryptionCollection configComparisonEncryptionCollection=
                ConfigComparisonEncryptionMapper.INSTANCE.daoFromDto(configuration);

        Update update = new Update();
        MongoHelper.appendFullProperties(update, configComparisonEncryptionCollection);

        Query query = Query.query(Criteria.where(APP_ID).is(configComparisonEncryptionCollection.getAppId())
                .and(AbstractComparisonDetails.Fields.operationId).is(configComparisonEncryptionCollection.getOperationId())
                .and(AbstractComparisonDetails.Fields.compareConfigType)
                .is(configComparisonEncryptionCollection.getCompareConfigType())
                .and(AbstractComparisonDetails.Fields.fsInterfaceId)
                .is(configComparisonEncryptionCollection.getFsInterfaceId())
                .and(AbstractComparisonDetails.Fields.dependencyId)
                .is(configComparisonEncryptionCollection.getDependencyId())
                .and(ConfigComparisonEncryptionCollection.Fields.path)
                .is(configComparisonEncryptionCollection.getPath()));
        ConfigComparisonEncryptionCollection dao=mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true).upsert(true), ConfigComparisonEncryptionCollection.class);
        return dao != null;
    }

    @Override
    public List<ComparisonEncryptionConfiguration> listBy(String appId, String operationId) {
        Query query = Query
                .query(Criteria.where(APP_ID).is(appId).and(AbstractComparisonDetails.Fields.operationId).is(operationId));
        List<ConfigComparisonEncryptionCollection> configComparisonEncryptionCollections =
                mongoTemplate.find(query, ConfigComparisonEncryptionCollection.class);
        return configComparisonEncryptionCollections.stream().map(ConfigComparisonEncryptionMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonEncryptionConfiguration> queryByInterfaceIdAndOperationId(String interfaceId, String operationId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(operationId)) {
            Criteria fsInterfaceConfigQuery = Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId);
            Criteria operationConfigQuery = Criteria.where(AbstractComparisonDetails.Fields.operationId).is(operationId)
                    .andOperator(Criteria.where(AbstractComparisonDetails.Fields.dependencyId).is(null));
            query.addCriteria(new Criteria().orOperator(fsInterfaceConfigQuery, operationConfigQuery));
        } else {
            query.addCriteria(Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId));
        }
        List<ConfigComparisonEncryptionCollection> configComparisonEncryptionCollections =
                mongoTemplate.find(query, ConfigComparisonEncryptionCollection.class);
        return configComparisonEncryptionCollections.stream().map(ConfigComparisonEncryptionMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    @Override
    public boolean insertList(List<ComparisonEncryptionConfiguration> configurationList) {
        if (CollectionUtils.isEmpty(configurationList)){
            return false;
        }
        List<ConfigComparisonEncryptionCollection> encryptionCollections = configurationList.stream()
                .map(ConfigComparisonEncryptionMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
        try {
            BulkOperations bulkOperations =
                    mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ConfigComparisonEncryptionCollection.class);
            for (ConfigComparisonEncryptionCollection encryptionCollection : encryptionCollections) {
                Update update = new Update();
                MongoHelper.appendFullProperties(update, encryptionCollection);

                Query query = Query.query(Criteria.where(APP_ID).is(encryptionCollection.getAppId())
                        .and(AbstractComparisonDetails.Fields.operationId).is(encryptionCollection.getOperationId())
                        .and(AbstractComparisonDetails.Fields.compareConfigType)
                        .is(encryptionCollection.getCompareConfigType()).and(AbstractComparisonDetails.Fields.fsInterfaceId)
                        .is(encryptionCollection.getFsInterfaceId()).and(AbstractComparisonDetails.Fields.dependencyId)
                        .is(encryptionCollection.getDependencyId())
                        .and(ConfigComparisonEncryptionCollection.Fields.path)
                        .is(encryptionCollection.getPath()));
                bulkOperations.upsert(query, update);
            }
            bulkOperations.execute();
        } catch (Exception e) {
            LogUtils.error(LOGGER, "encryption insertList failed! list:{}, exception:{}", configurationList, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean removeByAppId(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonEncryptionCollection.class);
        return remove.getDeletedCount() > 0;
    }
}
