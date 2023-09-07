//package com.arextest.web.core.repository.mongo;
//
//
//import com.arextest.web.core.repository.ConfigRepositoryField;
//import com.arextest.web.core.repository.ConfigRepositoryProvider;
//import com.arextest.web.core.repository.mongo.util.MongoHelper;
//import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
//import com.arextest.web.model.dao.mongodb.ServiceOperationCollection;
//import com.arextest.web.model.mapper.ServiceOperationMapper;
//import com.mongodb.client.result.DeleteResult;
//import com.mongodb.client.result.UpdateResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//@Repository
//public class ApplicationOperationConfigurationRepositoryImpl implements
//        ConfigRepositoryProvider<ApplicationOperationConfiguration>, ConfigRepositoryField {
//
//    private static final String RECORDED_CASE_COUNT = "recordedCaseCount";
//    private static final String SERVICE_ID = "serviceId";
//    private static final String OPERATION_RESPONSE = "operationResponse";
//
//    @Autowired
//    MongoTemplate mongoTemplate;
//
//
//    @Override
//    public List<ApplicationOperationConfiguration> list() {
//        throw new UnsupportedOperationException("this method is not implemented");
//    }
//
//    @Override
//    public List<ApplicationOperationConfiguration> listBy(String appId) {
//        Query query = Query.query(Criteria.where(APP_ID).is(appId));
//        List<ServiceOperationCollection> serviceOperationCollections = mongoTemplate.find(query, ServiceOperationCollection.class);
//        return serviceOperationCollections.stream().map(ServiceOperationMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean update(ApplicationOperationConfiguration configuration) {
//        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
//        Update update = MongoHelper.getConfigUpdate();
//        MongoHelper.appendSpecifiedProperties(update, configuration, STATUS, RECORDED_CASE_COUNT, OPERATION_RESPONSE);
//        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, ServiceOperationCollection.class);
//        return updateResult.getModifiedCount() > 0;
//    }
//
//    @Override
//    public boolean remove(ApplicationOperationConfiguration configuration) {
//        Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
//        DeleteResult remove = mongoTemplate.remove(query, ServiceOperationCollection.class);
//        return remove.getDeletedCount() > 0;
//    }
//
//    @Override
//    public boolean insert(ApplicationOperationConfiguration configuration) {
//        ServiceOperationCollection serviceOperationCollection = ServiceOperationMapper.INSTANCE.daoFromDto(configuration);
//        ServiceOperationCollection insert = mongoTemplate.insert(serviceOperationCollection);
//        if (insert.getId() != null) {
//            configuration.setId(insert.getId());
//        }
//        return insert.getId() != null;
//    }
//
//    public ApplicationOperationConfiguration listByOperationId(String operationId) {
//        Query query = Query.query(Criteria.where(DASH_ID).is(operationId));
//        ServiceOperationCollection serviceOperationCollection = mongoTemplate.findOne(query, ServiceOperationCollection.class);
//        return serviceOperationCollection == null ? null : ServiceOperationMapper.INSTANCE.dtoFromDao(serviceOperationCollection);
//    }
//
//    // the search of operation's based—info by serviceId
//    public List<ApplicationOperationConfiguration> operationBaseInfoList(String serviceId) {
//        Query query = Query.query(Criteria.where(SERVICE_ID).is(serviceId));
//        List<ServiceOperationCollection> serviceOperationCollections = mongoTemplate.find(query, ServiceOperationCollection.class);
//        return serviceOperationCollections.stream().map(ServiceOperationMapper.INSTANCE::baseInfoFromDao).collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean removeByAppId(String appId) {
//        Query query = Query.query(Criteria.where(APP_ID).is(appId));
//        DeleteResult remove = mongoTemplate.remove(query, ServiceOperationCollection.class);
//        return remove.getDeletedCount() > 0;
//    }
//}
