//package com.arextest.web.core.repository.mongo;
//
//
//import com.arextest.web.core.repository.ConfigRepositoryField;
//import com.arextest.web.core.repository.ConfigRepositoryProvider;
//import com.arextest.web.core.repository.mongo.util.MongoHelper;
//import com.arextest.web.model.contract.contracts.config.application.ApplicationConfiguration;
//import com.arextest.web.model.dao.mongodb.AppCollection;
//import com.arextest.web.model.mapper.AppMapper;
//import com.mongodb.client.result.DeleteResult;
//import com.mongodb.client.result.UpdateResult;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.stereotype.Repository;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//@Repository
//public class ApplicationConfigurationRepositoryImpl implements ConfigRepositoryProvider<ApplicationConfiguration>,
//        ConfigRepositoryField {
//
//    private static final String AGENT_VERSION = "agentVersion";
//    private static final String AGENT_EXT_VERSION = "agentExtVersion";
//
//    private static final String FEATURES = "features";
//
//    @Resource
//    private List<ConfigRepositoryProvider> configRepositoryProviders;
//
//
//
//    @Resource
//    MongoTemplate mongoTemplate;
//
//    @Override
//    public List<ApplicationConfiguration> list() {
//        Query query = new Query();
//        query.with(Sort.by(Sort.Order.desc(DASH_ID)));
//        List<AppCollection> appCollections = mongoTemplate.find(query, AppCollection.class);
//        return appCollections.stream().map(AppMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ApplicationConfiguration> listBy(String appId) {
//        Query query = Query.query(Criteria.where(APP_ID).is(appId));
//        List<AppCollection> appCollections = mongoTemplate.find(query, AppCollection.class);
//        return appCollections.stream().map(AppMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean update(ApplicationConfiguration configuration) {
//        Query query = Query.query(Criteria.where(APP_ID).is(configuration.getAppId()));
//        Update update = MongoHelper.getConfigUpdate();
//        MongoHelper.assertNull("update parameter is null", configuration.getAgentVersion(),
//                configuration.getAgentExtVersion(), configuration.getStatus());
//        update.set(AGENT_VERSION, configuration.getAgentVersion());
//        update.set(AGENT_EXT_VERSION, configuration.getAgentExtVersion());
//        update.set(STATUS, configuration.getStatus());
//        update.set(FEATURES, configuration.getFeatures());
//        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, AppCollection.class);
//        return updateResult.getModifiedCount() > 0;
//    }
//
//    @Override
//    public boolean remove(ApplicationConfiguration configuration) {
//        if (StringUtils.isBlank(configuration.getAppId())) {
//            return false;
//        }
//        for (ConfigRepositoryProvider configRepositoryProvider : configRepositoryProviders) {
//            configRepositoryProvider.removeByAppId(configuration.getAppId());
//        }
//        Query query = Query.query(Criteria.where(APP_ID).is(configuration.getAppId()));
//        DeleteResult remove = mongoTemplate.remove(query, AppCollection.class);
//        return remove.getDeletedCount() > 0;
//    }
//
//    @Override
//    public boolean insert(ApplicationConfiguration configuration) {
//        AppCollection appCollection = AppMapper.INSTANCE.daoFromDto(configuration);
//        AppCollection insert = mongoTemplate.insert(appCollection);
//        return insert.getId() != null;
//    }
//
//
//}
