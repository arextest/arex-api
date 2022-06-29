package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.MessagePreprocessRepository;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.dao.mongodb.MessagePreprocessCollection;
import com.arextest.report.model.dto.MessagePreprocessDto;
import com.arextest.report.model.mapper.MessagePreprocessMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MessagePreprocessRepositoryImpl implements MessagePreprocessRepository {
    private static final String PUBLISH_DATE = "publishDate";
    private static final String KEY = "key";
    private static final String PATH = "path";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public MessagePreprocessDto update(MessagePreprocessDto dto) {
        if (dto == null) {
            return null;
        }
        Update update = MongoHelper.getUpdate();
        update.setOnInsert(PUBLISH_DATE, System.currentTimeMillis());

        Query query = Query.query(Criteria.where(KEY).is(dto.getKey())
                .and(PATH).is(dto.getPath()));

        MessagePreprocessCollection dao = mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                MessagePreprocessCollection.class);
        return MessagePreprocessMapper.INSTANCE.dtoFromDao(dao);
    }

    @Override
    public List<MessagePreprocessDto> queryMessagesByKey(String key) {
        List<MessagePreprocessCollection> result = mongoTemplate.find(Query.query(Criteria.where(KEY).is(key)),
                MessagePreprocessCollection.class);
        return result.stream().map(MessagePreprocessMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }
}
