package com.arextest.report.core.repository.mongo;


import com.arextest.report.core.repository.ConfigRepositoryField;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.api.contracts.config.record.ServiceCollectConfiguration;
import com.arextest.report.model.dao.mongodb.RecordServiceConfigCollection;
import com.arextest.report.model.mapper.RecordServiceConfigMapper;
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
public class ServiceCollectConfigurationRepositoryImpl implements ConfigRepositoryProvider<ServiceCollectConfiguration>, ConfigRepositoryField {

    private static final String SAMPLE_RATE = "sampleRate";
    private static final String EXCLUDE_DEPENDENT_OPERATION_SET = "excludeDependentOperationSet";
    private static final String EXCLUDE_DEPENDENT_SERVICE_SET = "excludeDependentServiceSet";
    private static final String EXCLUDE_OPERATION_SET = "excludeOperationSet";
    private static final String INCLUDE_OPERATION_SET = "includeOperationSet";
    private static final String INCLUDE_SERVICE_SET = "includeServiceSet";
    private static final String ALLOW_DAY_OF_WEEKS = "allowDayOfWeeks";
    private static final String ALLOW_TIME_OF_DAY_FROM = "allowTimeOfDayFrom";
    private static final String ALLOW_TIME_OF_DAY_TO = "allowTimeOfDayTo";
    private static final String TIME_MOCK = "timeMock";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ServiceCollectConfiguration> list() {
        Query query = new Query();
        List<RecordServiceConfigCollection> recordServiceConfigCollections = mongoTemplate.find(query, RecordServiceConfigCollection.class);
        return recordServiceConfigCollections.stream().map(RecordServiceConfigMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public List<ServiceCollectConfiguration> listBy(String appId) {
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        List<RecordServiceConfigCollection> recordServiceConfigCollections = mongoTemplate.find(query, RecordServiceConfigCollection.class);
        return recordServiceConfigCollections.stream().map(RecordServiceConfigMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public boolean update(ServiceCollectConfiguration configuration) {
        Query query = Query.query(Criteria.where(APP_ID).is(configuration.getAppId()));
        Update update = MongoHelper.getConfigUpdate();
        MongoHelper.assertNull("update parameter is null", configuration.getAllowTimeOfDayFrom(),
                configuration.getAllowTimeOfDayTo());
        update.set(SAMPLE_RATE, configuration.getSampleRate());
        update.set(EXCLUDE_DEPENDENT_OPERATION_SET, configuration.getExcludeDependentOperationSet());
        update.set(EXCLUDE_DEPENDENT_SERVICE_SET, configuration.getExcludeDependentServiceSet());
        update.set(EXCLUDE_OPERATION_SET, configuration.getExcludeOperationSet());
        update.set(INCLUDE_OPERATION_SET, configuration.getIncludeOperationSet());
        update.set(INCLUDE_SERVICE_SET, configuration.getIncludeServiceSet());
        update.set(ALLOW_DAY_OF_WEEKS, configuration.getAllowDayOfWeeks());
        update.set(ALLOW_TIME_OF_DAY_FROM, configuration.getAllowTimeOfDayFrom());
        update.set(ALLOW_TIME_OF_DAY_TO, configuration.getAllowTimeOfDayTo());
        update.set(TIME_MOCK, configuration.isTimeMock());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, RecordServiceConfigCollection.class);
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public boolean remove(ServiceCollectConfiguration configuration) {
        Query query = Query.query(Criteria.where(APP_ID).is(configuration.getAppId()));
        DeleteResult remove = mongoTemplate.remove(query, RecordServiceConfigCollection.class);
        return remove.getDeletedCount() > 0;
    }

    @Override
    public boolean insert(ServiceCollectConfiguration configuration) {
        RecordServiceConfigCollection recordServiceConfigCollection = RecordServiceConfigMapper.INSTANCE.daoFromDto(configuration);
        RecordServiceConfigCollection insert = mongoTemplate.insert(recordServiceConfigCollection);
        return insert.getId() != null;
    }
}
