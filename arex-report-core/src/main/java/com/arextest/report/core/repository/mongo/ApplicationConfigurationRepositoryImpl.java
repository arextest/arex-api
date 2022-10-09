package com.arextest.report.core.repository.mongo;


import com.arextest.report.core.repository.ConfigRepositoryField;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.api.contracts.config.application.ApplicationConfiguration;
import com.arextest.report.model.dao.mongodb.AppCollection;
import com.arextest.report.model.mapper.AppMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public class ApplicationConfigurationRepositoryImpl implements ConfigRepositoryProvider<ApplicationConfiguration>, ConfigRepositoryField {

    private static final String AGENT_VERSION = "agentVersion";
    private static final String AGENT_EXT_VERSION = "agentExtVersion";

    private static final String FEATURES = "features";

    @Resource
    MongoTemplate mongoTemplate;

    @Override
    public List<ApplicationConfiguration> list() {
        Query query = new Query();
        List<AppCollection> appCollections = mongoTemplate.find(query, AppCollection.class);
        return appCollections.stream().map(AppMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<AppCollection> appCollections = mongoTemplate.find(query, AppCollection.class);
        return appCollections.stream().map(AppMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(ApplicationConfiguration configuration) {
        Query query = Query.query(Criteria.where(APP_ID).is(configuration.getAppId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.assertNull("update parameter is null", configuration.getAgentVersion(),
                configuration.getAgentExtVersion(), configuration.getStatus());
        update.set(AGENT_VERSION, configuration.getAgentVersion());
        update.set(AGENT_EXT_VERSION, configuration.getAgentExtVersion());
        update.set(STATUS, configuration.getStatus());
        update.set(FEATURES, configuration.getFeatures());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, AppCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ApplicationConfiguration configuration) {
        Query query = Query.query(Criteria.where(APP_ID).is(configuration.getAppId()));
        DeleteResult remove = mongoTemplate.remove(query, AppCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ApplicationConfiguration configuration) {
        AppCollection appCollection = AppMapper.INSTANCE.daoFromDto(configuration);
        AppCollection insert = mongoTemplate.insert(appCollection);
        return insert.getId() != null;
    }


}
