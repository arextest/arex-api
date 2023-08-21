package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsCategoryConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonExclusionsCategoryCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.mapper.ConfigComparisonExclusionsCategoryMapper;
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
public class ComparisonExclusionsCategoryConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonExclusionsCategoryConfiguration>, ConfigRepositoryField {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ComparisonExclusionsCategoryConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ComparisonExclusionsCategoryConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ConfigComparisonExclusionsCategoryCollection> collections =
            mongoTemplate.find(query, ConfigComparisonExclusionsCategoryCollection.class);
        return collections.stream().map(ConfigComparisonExclusionsCategoryMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonExclusionsCategoryConfiguration> listBy(String appId, String operationId) {
        Query query = Query
            .query(Criteria.where(APP_ID).is(appId).and(AbstractComparisonDetails.Fields.operationId).is(operationId));
        List<ConfigComparisonExclusionsCategoryCollection> configComparisonExclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonExclusionsCategoryCollection.class);
        return configComparisonExclusionsCollections.stream().map(ConfigComparisonExclusionsCategoryMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public List<ComparisonExclusionsCategoryConfiguration> queryByInterfaceIdAndOperationId(
        String interfaceId, String operationId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(operationId)) {
            query.addCriteria(new Criteria().orOperator(
                Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId),
                Criteria.where(AbstractComparisonDetails.Fields.operationId).is(operationId)));
        } else {
            query.addCriteria(Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId));
        }
        List<ConfigComparisonExclusionsCategoryCollection> configComparisonExclusionsCollections =
            mongoTemplate.find(query, ConfigComparisonExclusionsCategoryCollection.class);
        return configComparisonExclusionsCollections.stream().map(ConfigComparisonExclusionsCategoryMapper.INSTANCE::dtoFromDao)
            .collect(Collectors.toList());
    }

    @Override
    public boolean update(ComparisonExclusionsCategoryConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.appendSpecifiedProperties(update, configuration,
            ConfigComparisonExclusionsCategoryCollection.Fields.exclusionsCategory,
            AbstractComparisonDetails.Fields.expirationType,
            AbstractComparisonDetails.Fields.expirationDate);
        UpdateResult updateResult =
            mongoTemplate.updateMulti(query, update, ConfigComparisonExclusionsCategoryCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ComparisonExclusionsCategoryConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        return mongoTemplate.remove(query, ConfigComparisonExclusionsCategoryCollection.class).getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ComparisonExclusionsCategoryConfiguration configuration) {
        ConfigComparisonExclusionsCategoryCollection collection =
            ConfigComparisonExclusionsCategoryMapper.INSTANCE.daoFromDto(configuration);
        mongoTemplate.save(collection);
        return true;
    }
}
