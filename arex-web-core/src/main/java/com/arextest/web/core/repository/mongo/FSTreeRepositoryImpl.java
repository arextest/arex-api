package com.arextest.web.core.repository.mongo;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.arextest.web.core.repository.FSTreeRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.FSTreeCollection;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import com.arextest.web.model.mapper.FSTreeMapper;
import com.mongodb.client.result.DeleteResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FSTreeRepositoryImpl implements FSTreeRepository {
    private static final String WORKSPACE_NAME = "workspaceName";
    private static final String USERNAME = "userName";
    private static final String ROOTS = "roots";

    @Value("${arex.api.redis.lease-time}")
    private long redisLeaseTime;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public FSTreeDto initFSTree(FSTreeDto dto) {
        FSTreeCollection dao = FSTreeMapper.INSTANCE.daoFromDto(dto);
        MongoHelper.initInsertObject(dao);
        FSTreeCollection result = mongoTemplate.insert(dao);
        return FSTreeMapper.INSTANCE.dtoFromDao(result);
    }

    @Override
    public FSTreeDto updateFSTree(FSTreeDto dto) {
        Update update = MongoHelper.getUpdate();
        MongoHelper.appendFullProperties(update, dto);

        Query query = Query.query(Criteria.where(DASH_ID).is(dto.getId()));

        FSTreeCollection dao = mongoTemplate.findAndModify(query, update,
            FindAndModifyOptions.options().returnNew(true).upsert(true), FSTreeCollection.class);

        return FSTreeMapper.INSTANCE.dtoFromDao(dao);
    }

    @Override
    public FSTreeDto updateFSTree(String workspaceId, UnaryOperator<FSTreeDto> unaryOperator) {
        RLock lock = redissonClient.getLock(workspaceId);
        try {
            lock.lock(redisLeaseTime, TimeUnit.SECONDS);
            FSTreeDto workspace = queryFSTreeById(workspaceId);
            workspace = unaryOperator.apply(workspace);
            if (workspace == null) {
                return null;
            }
            FSTreeCollection dao = FSTreeMapper.INSTANCE.daoFromDto(workspace);
            return FSTreeMapper.INSTANCE.dtoFromDao(mongoTemplate.save(dao));
        } catch (Exception e) {
            LOGGER.error("Failed to update workspace.", e);
        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public FSTreeDto queryFSTreeById(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        FSTreeCollection dao = mongoTemplate.findOne(query, FSTreeCollection.class);
        return FSTreeMapper.INSTANCE.dtoFromDao(dao);
    }

    @Override
    public List<FSTreeDto> queryFSTreeByIds(Set<String> ids) {
        Set<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toSet());
        List<FSTreeCollection> daos =
            mongoTemplate.find(Query.query(Criteria.where(DASH_ID).in(objectIds)), FSTreeCollection.class);
        return daos.stream().map(FSTreeMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteFSTree(String id) {
        DeleteResult result =
            mongoTemplate.remove(Query.query(Criteria.where(DASH_ID).is(new ObjectId(id))), FSTreeCollection.class);
        return result.getDeletedCount() > 0;
    }
}
