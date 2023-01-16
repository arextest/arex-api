package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.FSCaseRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.FSCaseCollection;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.mapper.FSCaseMapper;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FSCaseRepositoryImpl implements FSCaseRepository {

    private static final String COMPARISON_MSG = "comparisonMsg";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String initCase(String parentId, Integer parentNodeType, String workspaceId, String name) {
        FSCaseCollection dao = new FSCaseCollection();
        MongoHelper.initInsertObject(dao);
        dao.setWorkspaceId(workspaceId);
        dao.setParentId(parentId);
        dao.setParentNodeType(parentNodeType);
        dao.setName(name);
        dao = mongoTemplate.insert(dao);
        return dao.getId();
    }

    @Override
    public Boolean removeCase(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        FSCaseCollection dao = mongoTemplate.findAndRemove(query, FSCaseCollection.class);
        return true;
    }

    @Override
    public Boolean removeCases(Set<String> ids) {
        Set<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toSet());
        Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
        DeleteResult result = mongoTemplate.remove(query, FSCaseCollection.class);
        return result.getDeletedCount() > 0;
    }

    @Override
    public FSCaseDto saveCase(FSCaseDto dto) {
        if (StringUtils.isEmpty(dto.getId())) {
            FSCaseCollection dao = FSCaseMapper.INSTANCE.daoFromDto(dto);
            dao = mongoTemplate.save(dao);
            return FSCaseMapper.INSTANCE.dtoFromDao(dao);
        } else {
            Query query = Query.query(Criteria.where(DASH_ID).is(new ObjectId(dto.getId())));
            Update update = MongoHelper.getUpdate();

            FSCaseCollection dao = FSCaseMapper.INSTANCE.daoFromDto(dto);
            MongoHelper.appendFullProperties(update, dao);

            FSCaseCollection result = mongoTemplate.findAndModify(query,
                    update,
                    FindAndModifyOptions.options().returnNew(true),
                    FSCaseCollection.class);
            return FSCaseMapper.INSTANCE.dtoFromDao(result);
        }
    }

    @Override
    public boolean updateCase(FSCaseDto dto) {
        if (StringUtils.isEmpty(dto.getId())) {
            return false;
        }
        Query query = Query.query(Criteria.where(DASH_ID).is(dto.getId()));
        Update update = MongoHelper.getUpdate();
        FSCaseCollection dao = FSCaseMapper.INSTANCE.daoFromDto(dto);
        MongoHelper.appendFullProperties(update, dao);
        mongoTemplate.findAndModify(query, update, FSCaseCollection.class);
        return true;
    }


    @Override
    public FSCaseDto queryCase(String id, boolean getCompareMsg) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        if (!getCompareMsg) {
            query.fields().exclude(COMPARISON_MSG);
        }
        FSCaseCollection dao = mongoTemplate.findOne(query, FSCaseCollection.class);
        if (dao == null) {
            return null;
        }
        return FSCaseMapper.INSTANCE.dtoFromDao(dao);
    }

    @Override
    public List<FSItemDto> queryCases(List<String> ids, boolean getCompareMsg) {
        List<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toList());
        Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
        if (!getCompareMsg) {
            query.fields().exclude(COMPARISON_MSG);
        }
        List<FSCaseCollection> daos = mongoTemplate.find(query, FSCaseCollection.class);
        return daos.stream().map(FSCaseMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public String duplicate(FSCaseDto dto) {
        FSCaseCollection dao = FSCaseMapper.INSTANCE.daoFromDto(dto);
        MongoHelper.initInsertObject(dao);
        dao = mongoTemplate.insert(dao);
        return dao.getId();
    }
}
