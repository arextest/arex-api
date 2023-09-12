package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.AbstractComparisonDetailsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonIgnoreCategoryCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.mapper.ConfigComparisonIgnoreCategoryMapper;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wildeslam.
 * @create 2023/8/18 15:02
 */
@Slf4j
@Repository
public class ComparisonIgnoreCategoryConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonIgnoreCategoryConfiguration> {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonIgnoreCategoryConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonIgnoreCategoryConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
        List<ConfigComparisonIgnoreCategoryCollection> collections =
            mongoTemplate.find(query, ConfigComparisonIgnoreCategoryCollection.class);
        return collections.stream().map(ConfigComparisonIgnoreCategoryMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonIgnoreCategoryConfiguration> listBy(String appId, String operationId) {
        Query query = Query
            .query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId).and(AbstractComparisonDetails.Fields.operationId).is(operationId));
        List<ConfigComparisonIgnoreCategoryCollection> configComparisonExclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonIgnoreCategoryCollection.class);
        return configComparisonExclusionsCollections.stream().map(ConfigComparisonIgnoreCategoryMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonIgnoreCategoryConfiguration> queryByInterfaceIdAndOperationId(
        String interfaceId, String operationId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(operationId)) {
            Criteria fsInterfaceConfigQuery = Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId);
            Criteria operationConfigQuery = Criteria.where(AbstractComparisonDetails.Fields.operationId).is(operationId)
                    .andOperator(Criteria.where(AbstractComparisonDetails.Fields.dependencyId).is(null));
            query.addCriteria(new Criteria().orOperator(fsInterfaceConfigQuery, operationConfigQuery));
        } else {
            query.addCriteria(Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId));
        }
        List<ConfigComparisonIgnoreCategoryCollection> configComparisonExclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonIgnoreCategoryCollection.class);
        return configComparisonExclusionsCollections.stream().map(ConfigComparisonIgnoreCategoryMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonIgnoreCategoryConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration,
            ConfigComparisonIgnoreCategoryCollection.Fields.ignoreCategory,
            AbstractComparisonDetails.Fields.expirationType,
            AbstractComparisonDetails.Fields.expirationDate);
        UpdateResult updateResult =
            mongoTemplate.updateMulti(query, update, ConfigComparisonIgnoreCategoryCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ComparisonIgnoreCategoryConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        return mongoTemplate.remove(query, ConfigComparisonIgnoreCategoryCollection.class).getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ComparisonIgnoreCategoryConfiguration configuration) {
        ConfigComparisonIgnoreCategoryCollection collection =
            ConfigComparisonIgnoreCategoryMapper.INSTANCE.daoFromDto(configuration);
        mongoTemplate.save(collection);
        return true;
    }
}
