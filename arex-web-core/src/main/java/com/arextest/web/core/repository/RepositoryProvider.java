package com.arextest.web.core.repository;

import org.springframework.data.mongodb.core.query.Update;
public interface RepositoryProvider {

  String DATA_CHANGE_CREATE_TIME = "dataChangeCreateTime";
  String DATA_CHANGE_UPDATE_TIME = "dataChangeUpdateTime";
  String DASH_ID = "_id";

  default Update getConfigUpdate() {
      Update update = new Update();
      update.set("dataChangeUpdateTime", System.currentTimeMillis());
      return update;
  }
}
