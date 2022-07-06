package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.UserRepository;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.dao.mongodb.UserCollection;
import com.arextest.report.model.dto.UserDto;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserRepositoryImpl implements UserRepository {

    private static final String USER_NAME = "userName";
    private static final String VERIFICATION_CODE = "verificationCode";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean saveVerificationCode(UserDto user) {
        Query query = Query.query(Criteria.where(USER_NAME).is(user.getEmail()));
        Update update = MongoHelper.getUpdate();
        update.set(VERIFICATION_CODE, user.getVerificationCode());
        mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                UserCollection.class);
        return true;
    }
    @Override
    public Boolean verify(String email, String verificationCode) {
        Query query = Query.query(Criteria.where(USER_NAME).is(email)
                .and(VERIFICATION_CODE).is(verificationCode)
                .and(DATA_CHANGE_UPDATE_TIME).gt(System.currentTimeMillis() - 5 * 60 * 1000));
        return mongoTemplate.exists(query, UserCollection.class);
    }
}
