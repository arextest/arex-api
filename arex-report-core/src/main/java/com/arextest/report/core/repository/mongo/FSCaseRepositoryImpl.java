package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.FSCaseRepository;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.dao.mongodb.FSCaseCollection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FSCaseRepositoryImpl implements FSCaseRepository {

    private static final String DASH_ID = "_id";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String initCase() {
        FSCaseCollection dao = new FSCaseCollection();
        MongoHelper.initInsertObject(dao);
        dao = mongoTemplate.insert(dao);
        return dao.getId();
    }
    @Override
    public Boolean removeCases(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        FSCaseCollection dao = mongoTemplate.findAndRemove(query, FSCaseCollection.class);
        return true;
    }
}
