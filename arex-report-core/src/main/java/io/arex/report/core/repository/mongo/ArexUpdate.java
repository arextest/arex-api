package io.arex.report.core.repository.mongo;

import org.springframework.data.mongodb.core.query.Update;


public class ArexUpdate {
    private static final String DATA_CHANGE_UPDATE_TIME = "dataChangeUpdateTime";

    public static Update getUpdate() {
        Update update = new Update();
        update.set(DATA_CHANGE_UPDATE_TIME, System.currentTimeMillis());
        return update;
    }
}
