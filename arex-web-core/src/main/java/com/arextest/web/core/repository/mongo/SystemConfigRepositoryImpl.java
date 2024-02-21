package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.SystemConfigRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.config.SystemConfiguration;
import com.arextest.web.model.dao.mongodb.SystemConfigCollection;
import com.arextest.web.model.dao.mongodb.SystemConfigurationCollection;
import com.arextest.web.model.mapper.SystemConfigurationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wildeslam.
 * @create 2023/9/25 18:51
 */
@Slf4j
@Repository
public class SystemConfigRepositoryImpl implements SystemConfigRepository {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public boolean saveConfig(SystemConfiguration systemConfig) {
    Query query = Query.query(Criteria.where(SystemConfigurationCollection.Fields.key).is(systemConfig.getKey()));
    Update update = MongoHelper.getUpdate();
    MongoHelper.appendFullProperties(update, systemConfig);
    mongoTemplate.findAndModify(query, update, SystemConfigCollection.class);
    return true;
  }

  @Override
  public List<SystemConfiguration> getAllSystemConfigList() {
    return mongoTemplate.findAll(SystemConfigurationCollection.class)
        .stream()
        .map(SystemConfigurationMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public SystemConfiguration getSystemConfigByKey(String key) {
    Query query = Query.query(Criteria.where(SystemConfigurationCollection.Fields.key).is(key));
    SystemConfigurationCollection systemConfigurationCollection = mongoTemplate.findOne(query,
        SystemConfigurationCollection.class);
    return SystemConfigurationMapper.INSTANCE.dtoFromDao(systemConfigurationCollection);
  }
}
