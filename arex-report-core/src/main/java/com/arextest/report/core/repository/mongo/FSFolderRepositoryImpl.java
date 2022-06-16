package com.arextest.report.core.repository.mongo;


import com.arextest.report.core.repository.FSFolderRepository;
import com.arextest.report.model.dao.mongodb.FSCaseCollection;
import com.arextest.report.model.dao.mongodb.FSFolderCollection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FSFolderRepositoryImpl implements FSFolderRepository {

    private static final String DASH_ID = "_id";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String initFolder() {
        FSFolderCollection dao = new FSFolderCollection();
        ArexUpdate.initInsertObject(dao);
        dao = mongoTemplate.insert(dao);
        return dao.getId();
    }
    @Override
    public Boolean removeFolder(String id) {
        Query query = Query.query(Criteria.where(DASH_ID).is(id));
        FSFolderCollection dao = mongoTemplate.findAndRemove(query, FSFolderCollection.class);
        return true;
    }
}