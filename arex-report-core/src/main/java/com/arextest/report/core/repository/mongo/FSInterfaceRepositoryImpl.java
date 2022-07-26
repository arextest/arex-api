package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.FSInterfaceRepository;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.dao.mongodb.FSInterfaceCollection;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.mapper.FSInterfaceMapper;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class FSInterfaceRepositoryImpl implements FSInterfaceRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String initInterface(String parentId, Integer parentNodeType) {
        FSInterfaceCollection dao = new FSInterfaceCollection();
        MongoHelper.initInsertObject(dao);
        dao.setParentId(parentId);
        dao.setParentNodeType(parentNodeType);
        dao = mongoTemplate.insert(dao);
        return dao.getId();
    }
    @Override
    public Boolean removeInterface(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        FSInterfaceCollection dao = mongoTemplate.findAndRemove(query, FSInterfaceCollection.class);
        return dao != null ? true : false;
    }

    @Override
    public Boolean removeInterfaces(Set<String> ids) {
        Set<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toSet());
        Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
        DeleteResult result = mongoTemplate.remove(query, FSInterfaceCollection.class);
        return result.getDeletedCount() > 0;
    }

    @Override
    public FSInterfaceDto saveInterface(FSInterfaceDto interfaceDto) {
        Query query = Query.query(Criteria.where(DASH_ID).is(interfaceDto.getId()));
        Update update = MongoHelper.getUpdate();

        FSInterfaceCollection dao = FSInterfaceMapper.INSTANCE.daoFromDto(interfaceDto);
        MongoHelper.appendFullProperties(update, dao);

        FSInterfaceCollection result = mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                FSInterfaceCollection.class);

        return FSInterfaceMapper.INSTANCE.dtoFromDao(result);
    }

    @Override
    public FSInterfaceDto queryInterface(String id) {
        FSInterfaceCollection dao = mongoTemplate.findById(new ObjectId(id), FSInterfaceCollection.class);
        if (dao == null) {
            return null;
        }
        return FSInterfaceMapper.INSTANCE.dtoFromDao(dao);
    }
    @Override
    public List<FSInterfaceDto> queryInterfaces(Set<String> ids) {
        Set<ObjectId> objectIds = ids.stream().map(id -> new ObjectId(id)).collect(Collectors.toSet());
        Query query = Query.query(Criteria.where(DASH_ID).in(objectIds));
        List<FSInterfaceCollection> daos = mongoTemplate.find(query, FSInterfaceCollection.class);
        return daos.stream().map(FSInterfaceMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public String duplicate(FSInterfaceDto dto) {
        FSInterfaceCollection dao = FSInterfaceMapper.INSTANCE.daoFromDto(dto);
        MongoHelper.initInsertObject(dao);
        dao = mongoTemplate.insert(dao);
        return dao.getId();
    }
}
