package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonIgnoreCategoryCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.dao.mongodb.entity.CategoryDetailDao;
import com.arextest.web.model.mapper.ConfigComparisonIgnoreCategoryMapper;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wildeslam.
 * @create 2023/8/18 15:02
 */
@Slf4j
@Repository
public class ComparisonIgnoreCategoryConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonIgnoreCategoryConfiguration> {

  @PostConstruct
  public void init() {
    try {
      cleanOldData();
    } catch (Exception e) {
      LOGGER.error("clean old data failed", e);
    }

  }

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public List<ComparisonIgnoreCategoryConfiguration> list() {
    throw new UnsupportedOperationException("this method is not implemented");
  }

  @Override
  public List<ComparisonIgnoreCategoryConfiguration> listBy(String appId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
    List<ConfigComparisonIgnoreCategoryCollection> collections =
        mongoTemplate.find(query, ConfigComparisonIgnoreCategoryCollection.class);
    return collections.stream().map(ConfigComparisonIgnoreCategoryMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonIgnoreCategoryConfiguration> listBy(String appId, String operationId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId)
        .and(AbstractComparisonDetails.Fields.operationId).is(operationId).and(
            AbstractComparisonDetails.Fields.compareConfigType)
        .is(CompareConfigType.REPLAY_MAIN.getCodeValue()));
    List<ConfigComparisonIgnoreCategoryCollection> configComparisonExclusionsCollections =
        mongoTemplate.find(query, ConfigComparisonIgnoreCategoryCollection.class);
    return configComparisonExclusionsCollections.stream()
        .map(ConfigComparisonIgnoreCategoryMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonIgnoreCategoryConfiguration> queryByInterfaceIdAndOperationId(
      String interfaceId,
      String operationId) {
    Query query = new Query();
    if (StringUtils.isNotBlank(operationId)) {
      Criteria fsInterfaceConfigQuery =
          Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId);
      Criteria operationConfigQuery = Criteria.where(AbstractComparisonDetails.Fields.operationId)
          .is(operationId)
          .andOperator(Criteria.where(AbstractComparisonDetails.Fields.dependencyId).is(null));
      query.addCriteria(new Criteria().orOperator(fsInterfaceConfigQuery, operationConfigQuery));
    } else {
      query.addCriteria(
          Criteria.where(AbstractComparisonDetails.Fields.fsInterfaceId).is(interfaceId));
    }
    List<ConfigComparisonIgnoreCategoryCollection> configComparisonExclusionsCollections =
        mongoTemplate.find(query, ConfigComparisonIgnoreCategoryCollection.class);
    return configComparisonExclusionsCollections.stream()
        .map(ConfigComparisonIgnoreCategoryMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean update(ComparisonIgnoreCategoryConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    Update update = MongoHelper.getConfigUpdate();
    MongoHelper.appendSpecifiedProperties(update, configuration,
        ConfigComparisonIgnoreCategoryCollection.Fields.ignoreCategoryDetail,
        AbstractComparisonDetails.Fields.expirationType,
        AbstractComparisonDetails.Fields.expirationDate);
    UpdateResult updateResult =
        mongoTemplate.updateMulti(query, update, ConfigComparisonIgnoreCategoryCollection.class);
    return updateResult.getModifiedCount() > 0;
  }

  @Override
  public boolean remove(ComparisonIgnoreCategoryConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    return mongoTemplate.remove(query, ConfigComparisonIgnoreCategoryCollection.class)
        .getDeletedCount() > 0;
  }

  @Override
  public boolean insert(ComparisonIgnoreCategoryConfiguration configuration) {
    ConfigComparisonIgnoreCategoryCollection collection =
        ConfigComparisonIgnoreCategoryMapper.INSTANCE.daoFromDto(configuration);
    mongoTemplate.save(collection);
    return true;
  }

  public void cleanOldData() {
    Query query = Query.query(Criteria.where(ConfigComparisonIgnoreCategoryCollection.Fields.ignoreCategory).ne(null));
    List<ConfigComparisonIgnoreCategoryCollection> oldData =
        mongoTemplate.findAllAndRemove(query, ConfigComparisonIgnoreCategoryCollection.class).stream()
            .filter(config -> CollectionUtils.isNotEmpty(config.getIgnoreCategory()))
            .collect(Collectors.toList());

    List<ConfigComparisonIgnoreCategoryCollection> newData = new ArrayList<>();
    oldData.forEach(oldConfig -> {
      oldConfig.getIgnoreCategory().forEach(ignoreCategory -> {
        ConfigComparisonIgnoreCategoryCollection newConfigItem = convertToNewConfig(oldConfig);
        CategoryDetailDao categoryDetailDao = new CategoryDetailDao();
        categoryDetailDao.setOperationType(ignoreCategory);
        newConfigItem.setIgnoreCategoryDetail(categoryDetailDao);

        newData.add(newConfigItem);
      });
    });
    mongoTemplate.insertAll(newData);
  }

  private ConfigComparisonIgnoreCategoryCollection convertToNewConfig(
      ConfigComparisonIgnoreCategoryCollection oldConfig) {
    ConfigComparisonIgnoreCategoryCollection newConfig = new ConfigComparisonIgnoreCategoryCollection();
    MongoHelper.initInsertObject(newConfig);
    newConfig.setAppId(oldConfig.getAppId());
    newConfig.setOperationId(oldConfig.getOperationId());
    newConfig.setExpirationType(oldConfig.getExpirationType());
    newConfig.setExpirationDate(oldConfig.getExpirationDate());
    return newConfig;
  }
}
