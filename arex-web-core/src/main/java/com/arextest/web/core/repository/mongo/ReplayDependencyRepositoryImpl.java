package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ReplayDependencyRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.ReplayDependencyCollection;
import com.arextest.web.model.dto.ReplayDependencyDto;
import com.arextest.web.model.mapper.ReplayDependencyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Slf4j
@Repository
public class ReplayDependencyRepositoryImpl implements ReplayDependencyRepository {
    private static final String OPERATION_ID = "operationId";
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean saveDependency(ReplayDependencyDto replayDependency) {
        try {
            Query query = new Query().addCriteria(Criteria.where(OPERATION_ID).is(replayDependency.getOperationId()));
            ReplayDependencyCollection collection = ReplayDependencyMapper.INSTANCE.daoFromDto(replayDependency);
            Update update = new Update();
            MongoHelper.appendFullProperties(update, collection);
            mongoTemplate.upsert(query, update, ReplayDependencyCollection.class);
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "failed to save Dependency", e);
        }
        return false;
    }

    @Override
    public ReplayDependencyDto queryDependency(String operationId) {
        Query query = new Query().addCriteria(Criteria.where(OPERATION_ID).is(operationId));

        return ReplayDependencyMapper.INSTANCE.dtoFromDao(
                mongoTemplate.findOne(query, ReplayDependencyCollection.class));
    }
}
