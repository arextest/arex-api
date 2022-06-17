package com.arextest.report.core.repository.mongo.util;

import com.arextest.report.core.repository.RepositoryProvider;
import com.arextest.report.model.dao.mongodb.ModelBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;

@Slf4j
public class MongoHelper {
    public static Update getUpdate() {
        Update update = new Update();
        update.set(RepositoryProvider.DATA_CHANGE_UPDATE_TIME, System.currentTimeMillis());
        return update;
    }

    public static void appendFullProperties(Update update, Object obj) {
        for (java.lang.reflect.Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                update.set(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                LOGGER.error(String.format("Class:[%s]. failed to get field %s",
                        obj.getClass().getName(),
                        field.getName()), e);
            }
        }
    }

    public static <T extends ModelBase> T initInsertObject(T obj) {
        obj.setDataChangeCreateTime(System.currentTimeMillis());
        obj.setDataChangeUpdateTime(System.currentTimeMillis());
        return obj;
    }
}
