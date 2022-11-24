package com.arextest.web.core.repository.mongo;


import com.arextest.web.core.repository.ConfigRepositoryField;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.application.ApplicationServiceConfiguration;
import com.arextest.web.model.dao.mongodb.ServiceCollection;
import com.arextest.web.model.mapper.ServiceMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ApplicationServiceConfigurationRepositoryImpl implements
        ConfigRepositoryProvider<ApplicationServiceConfiguration>, ConfigRepositoryField {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

    @Override
    public List<ApplicationServiceConfiguration> list() {
        throw new UnsupportedOperationException("this method is not implemented");
    }

    @Override
    public List<ApplicationServiceConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<ServiceCollection> serviceCollections = mongoTemplate.find(query, ServiceCollection.class);
        List<ApplicationServiceConfiguration> applicationServiceConfigurations = serviceCollections.stream().map(ServiceMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
        applicationServiceConfigurations.forEach(item -> item.setOperationList(applicationOperationConfigurationRepository.operationBaseInfoList(item.getId())));
        return applicationServiceConfigurations;
    }

    @Override
    public boolean update(ApplicationServiceConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.assertNull("update parameter is null", configuration.getStatus());
        update.set(STATUS, configuration.getStatus());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ServiceCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ApplicationServiceConfiguration configuration) {
        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
        DeleteResult remove = mongoTemplate.remove(query, ServiceCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ApplicationServiceConfiguration configuration) {
        ServiceCollection serviceCollection = ServiceMapper.INSTANCE.daoFromDto(configuration);
        ServiceCollection insert = mongoTemplate.insert(serviceCollection);
        if (insert.getId() != null) {
            configuration.setId(insert.getId());
        }
        return insert.getId() != null;
    }

    @Override
    public long count(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        return mongoTemplate.count(query, ServiceCollection.class);
    }
}
