package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.RepositoryProvider;
import org.springframework.data.mongodb.core.query.Update;


public class ArexUpdate {

    public static Update getUpdate() {
        Update update = new Update();
        update.set(RepositoryProvider.DATA_CHANGE_UPDATE_TIME, System.currentTimeMillis());
        return update;
    }
}
