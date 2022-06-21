package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.FSInterfaceRepository;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.dao.mongodb.FSInterfaceCollection;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.mapper.FSInterfaceMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class FSInterfaceRepositoryImpl implements FSInterfaceRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String initInterface() {
        FSInterfaceCollection dao = new FSInterfaceCollection();
        MongoHelper.initInsertObject(dao);
        dao = mongoTemplate.insert(dao);
        return dao.getId();
    }
    @Override
    public Boolean removeInterface(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        FSInterfaceCollection dao = mongoTemplate.findAndRemove(query, FSInterfaceCollection.class);
        return true;
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
}
