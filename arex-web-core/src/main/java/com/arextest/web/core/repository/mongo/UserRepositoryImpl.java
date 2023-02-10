package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.UserRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.UserCollection;
import com.arextest.web.model.dto.UserDto;
import com.arextest.web.model.mapper.UserMapper;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {

    private static final String USER_NAME = "userName";
    private static final String VERIFICATION_CODE = "verificationCode";
    private static final String VERIFICATION_TIME = "verificationTime";
    private static final String FAVORITE_APPS = "favoriteApps";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean saveUser(UserDto user) {
        Query query = Query.query(Criteria.where(USER_NAME).is(user.getUserName()));
        Update update = MongoHelper.getUpdate();
        MongoHelper.appendFullProperties(update, user);
        mongoTemplate.findAndModify(query,
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                UserCollection.class);
        return true;
    }

    @Override
    public Boolean verify(String userName, String verificationCode) {
        Query query = Query.query(Criteria.where(USER_NAME).is(userName)
                .and(VERIFICATION_CODE).is(verificationCode)
                .and(VERIFICATION_TIME).gt(System.currentTimeMillis() - 5 * 60 * 1000));
        return mongoTemplate.exists(query, UserCollection.class);
    }

    @Override
    public UserDto queryUserProfile(String userName) {
        Query query = Query.query(Criteria.where(USER_NAME).is(userName));
        query.fields().exclude(VERIFICATION_CODE);
        UserCollection dao = mongoTemplate.findOne(query, UserCollection.class);
        return UserMapper.INSTANCE.dtoFromDao(dao);
    }

    @Override
    public Boolean updateUserProfile(UserDto user) {
        Query query = Query.query(Criteria.where(USER_NAME).is(user.getUserName()));
        Update update = MongoHelper.getUpdate();
        MongoHelper.appendFullProperties(update, user);
        try {
            mongoTemplate.findAndModify(query,
                    update,
                    FindAndModifyOptions.options().returnNew(true).upsert(true),
                    UserCollection.class);
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "failed to update user profile.", e);
            return false;
        }
    }

    @Override
    public Boolean existUserName(String userName) {
        Query query = Query.query(Criteria.where(USER_NAME).is(userName));
        return mongoTemplate.exists(query, UserCollection.class);
    }

    @Override
    public Boolean insertUserFavoriteApp(String userName, String favoriteApp) {
        Query query = Query.query(Criteria.where(USER_NAME).is(userName));
        Update update = MongoHelper.getUpdate();
        update.addToSet(FAVORITE_APPS, favoriteApp);
        UpdateResult upsert = mongoTemplate.upsert(query, update, UserCollection.class);
        return upsert.getModifiedCount() > 0;
    }

    @Override
    public Boolean removeUserFavoriteApp(String userName, String favoriteApp) {
        Query query = Query.query(Criteria.where(USER_NAME).is(userName));
        Update update = MongoHelper.getUpdate();
        update.pull(FAVORITE_APPS, favoriteApp);
        UpdateResult upsert = mongoTemplate.upsert(query, update, UserCollection.class);
        return upsert.getModifiedCount() > 0;
    }
}
