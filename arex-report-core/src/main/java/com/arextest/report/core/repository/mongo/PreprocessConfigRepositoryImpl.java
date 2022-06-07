package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.PreprocessConfigRepository;
import com.arextest.report.model.dao.mongodb.PreprocessConfigCollection;
import com.arextest.report.model.dto.PreprocessConfigDto;
import com.arextest.report.model.mapper.PreprocessConfigMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PreprocessConfigRepositoryImpl implements PreprocessConfigRepository {
    private static final String NAME = "name";
    private static final String INDEX = "index";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PreprocessConfigDto updateIndex(String name, String index) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        if (StringUtils.isEmpty(index)) {
            return null;
        }
        Query query = Query.query(Criteria.where(NAME).is(name));
        Update update = ArexUpdate.getUpdate();
        update.set(INDEX, index);
        PreprocessConfigCollection dao = mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                PreprocessConfigCollection.class);
        return PreprocessConfigMapper.INSTANCE.dtoFromDao(dao);
    }

    @Override
    public PreprocessConfigDto queryConfig(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Query query = Query.query(Criteria.where(NAME).is(name));
        PreprocessConfigCollection dao = mongoTemplate.findOne(query, PreprocessConfigCollection.class);
        return PreprocessConfigMapper.INSTANCE.dtoFromDao(dao);
    }
}
