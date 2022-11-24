package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.EnvironmentRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.EnvironmentCollection;
import com.arextest.web.model.dto.EnvironmentDto;
import com.arextest.web.model.mapper.EnvironmentMapper;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class EnvironmentRepositoryImpl implements EnvironmentRepository {

    private static final String WORKSPACE_ID = "workspaceId";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public EnvironmentDto initEnvironment(EnvironmentDto environment) {
        EnvironmentCollection dao = EnvironmentMapper.INSTANCE.daoFromDto(environment);
        MongoHelper.initInsertObject(dao);
        EnvironmentCollection result = mongoTemplate.insert(dao);
        return EnvironmentMapper.INSTANCE.dtoFromDao(result);
    }
    @Override
    public EnvironmentDto saveEnvironment(EnvironmentDto environment) {
        EnvironmentCollection dao = EnvironmentMapper.INSTANCE.daoFromDto(environment);

        Query query = Query.query(Criteria.where(DASH_ID).is(environment.getId()));
        Update update = MongoHelper.getUpdate();
        MongoHelper.appendFullProperties(update, dao);

        EnvironmentCollection result = mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                EnvironmentCollection.class);
        return EnvironmentMapper.INSTANCE.dtoFromDao(result);
    }
    @Override
    public boolean removeEnvironment(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        EnvironmentCollection dao = mongoTemplate.findAndRemove(query, EnvironmentCollection.class);
        return dao != null ? true : false;
    }
    @Override
    public List<EnvironmentDto> queryEnvsByWorkspace(String workspaceId) {
        Query query = Query.query(Criteria.where(WORKSPACE_ID).is(workspaceId));
        List<EnvironmentCollection> envs = mongoTemplate.find(query, EnvironmentCollection.class);
        return EnvironmentMapper.INSTANCE.dtoFromDaoList(envs);
    }
    @Override
    public EnvironmentDto queryById(String id) {
        EnvironmentCollection dao = mongoTemplate.findById(new ObjectId(id), EnvironmentCollection.class);
        return EnvironmentMapper.INSTANCE.dtoFromDao(dao);
    }
}
