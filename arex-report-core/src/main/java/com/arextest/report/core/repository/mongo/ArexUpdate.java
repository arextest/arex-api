package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.RepositoryProvider;
import com.arextest.report.model.dao.mongodb.ModelBase;
import org.springframework.data.mongodb.core.query.Update;


public class ArexUpdate {
    public static Update getUpdate() {
        Update update = new Update();
        update.set(RepositoryProvider.DATA_CHANGE_UPDATE_TIME, System.currentTimeMillis());
        return update;
    }

    public static <T extends ModelBase> T initInsertObject(T obj) {
        obj.setDataChangeCreateTime(System.currentTimeMillis());
        obj.setDataChangeUpdateTime(System.currentTimeMillis());
        return obj;
    }
}
