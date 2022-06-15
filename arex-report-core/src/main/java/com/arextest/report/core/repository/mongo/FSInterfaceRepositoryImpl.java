package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.FSInterfaceRepository;
import com.arextest.report.model.dao.mongodb.FSCaseCollection;
import com.arextest.report.model.dao.mongodb.FSInterfaceCollection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FSInterfaceRepositoryImpl implements FSInterfaceRepository {

    private static final String DASH_ID = "_id";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String initInterface() {
        FSInterfaceCollection dao = new FSInterfaceCollection();
        ArexUpdate.initInsertObject(dao);
        dao = mongoTemplate.insert(dao);
        return dao.getId();
    }
    @Override
    public Boolean removeInterface(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        FSInterfaceCollection dao = mongoTemplate.findAndRemove(query, FSInterfaceCollection.class);
        return true;
    }
}
