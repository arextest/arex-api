package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonTransformCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails.Fields;
import com.arextest.web.model.dao.mongodb.entity.TransformDetailDao;
import com.arextest.web.model.mapper.ConfigComparisonTransformMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Slf4j
@Repository
public class ComparisonTransformConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonTransformConfiguration> {

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public List<ComparisonTransformConfiguration> list() {
    throw new UnsupportedOperationException("this method is not implemented");
  }

  @Override
  public List<ComparisonTransformConfiguration> listBy(String appId) {
    Query query = Query.query(Criteria.where(Fields.appId).is(appId));
    List<ConfigComparisonTransformCollection> configComparisonTransformCollections =
        mongoTemplate.find(query, ConfigComparisonTransformCollection.class);
    return configComparisonTransformCollections.stream()
        .map(ConfigComparisonTransformMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonTransformConfiguration> listBy(String appId, String operationId) {
    Query query = Query.query(
        Criteria.where(Fields.appId).is(appId)
            .and(Fields.operationId).is(operationId)
            .and(Fields.compareConfigType).is(CompareConfigType.REPLAY_MAIN.getCodeValue())
    );
    List<ConfigComparisonTransformCollection> configComparisonTransformCollections =
        mongoTemplate.find(query, ConfigComparisonTransformCollection.class);
    return configComparisonTransformCollections.stream()
        .map(ConfigComparisonTransformMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonTransformConfiguration> queryByInterfaceIdAndOperationId(
      String interfaceId, String operationId) {
    Query query = new Query();
    if (StringUtils.isNotBlank(operationId)) {
      Criteria fsInterfaceConfigQuery = Criteria.where(Fields.fsInterfaceId).is(interfaceId);
      Criteria operationConfigQuery = Criteria.where(Fields.operationId).is(operationId)
          .andOperator(Criteria.where(Fields.dependencyId).is(null));
      query.addCriteria(new Criteria().orOperator(fsInterfaceConfigQuery, operationConfigQuery));
    } else {
      query.addCriteria(Criteria.where(Fields.fsInterfaceId).is(interfaceId));
    }
    List<ConfigComparisonTransformCollection> configComparisonTransformCollections =
        mongoTemplate.find(query, ConfigComparisonTransformCollection.class);
    return configComparisonTransformCollections.stream()
        .map(ConfigComparisonTransformMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean update(ComparisonTransformConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    Update update = MongoHelper.getConfigUpdate();
    MongoHelper.appendSpecifiedProperties(update, configuration,
//        ConfigComparisonTransformCollection.Fields.nodePath,
//        ConfigComparisonTransformCollection.Fields.transformMethods,
        ConfigComparisonTransformCollection.Fields.transformDetail,
        Fields.expirationType,
        Fields.expirationDate);
    UpdateResult updateResult =
        mongoTemplate.updateMulti(query, update, ConfigComparisonTransformCollection.class);
    return updateResult.getModifiedCount() > 0;
  }

  @Override
  public boolean remove(ComparisonTransformConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonTransformCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public boolean insert(ComparisonTransformConfiguration configuration) {
    ConfigComparisonTransformCollection configComparisonTransformCollection =
        ConfigComparisonTransformMapper.INSTANCE.daoFromDto(configuration);

    Update update = new Update();
    MongoHelper.appendFullProperties(update, configComparisonTransformCollection);

    Query query = Query.query(Criteria.where(Fields.appId)
        .is(configComparisonTransformCollection.getAppId())
        .and(Fields.operationId)
        .is(configComparisonTransformCollection.getOperationId())
        .and(Fields.compareConfigType)
        .is(configComparisonTransformCollection.getCompareConfigType())
        .and(Fields.fsInterfaceId)
        .is(configComparisonTransformCollection.getFsInterfaceId())
        .and(Fields.dependencyId)
        .is(configComparisonTransformCollection.getDependencyId())
        .and(MongoHelper.appendDot(ConfigComparisonTransformCollection.Fields.transformDetail,
            TransformDetailDao.Fields.nodePath))
        .is(configComparisonTransformCollection.getTransformDetail().getNodePath()));

    ConfigComparisonTransformCollection dao = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(true).upsert(true),
        ConfigComparisonTransformCollection.class);
    return dao != null;
  }

  @Override
  public boolean insertList(List<ComparisonTransformConfiguration> configurationList) {
    if (CollectionUtils.isEmpty(configurationList)) {
      return false;
    }
    List<ConfigComparisonTransformCollection> transformCollections = configurationList.stream()
        .map(ConfigComparisonTransformMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
    try {
      BulkOperations bulkOperations =
          mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
              ConfigComparisonTransformCollection.class);
      for (ConfigComparisonTransformCollection transformCollection : transformCollections) {
        Update update = new Update();
        MongoHelper.appendFullProperties(update, transformCollection);

        Query query = Query.query(Criteria.where(Fields.appId).is(transformCollection.getAppId())
            .and(Fields.operationId).is(transformCollection.getOperationId())
            .and(Fields.compareConfigType).is(transformCollection.getCompareConfigType())
            .and(Fields.fsInterfaceId).is(transformCollection.getFsInterfaceId())
            .and(Fields.dependencyId).is(transformCollection.getDependencyId())
            .and(MongoHelper.appendDot(ConfigComparisonTransformCollection.Fields.transformDetail,
                TransformDetailDao.Fields.nodePath))
            .is(transformCollection.getTransformDetail().getNodePath()));
        bulkOperations.upsert(query, update);
      }
      bulkOperations.execute();
    } catch (Exception e) {
      LogUtils.error(LOGGER, "transform insertList failed! list:{}, exception:{}",
          configurationList, e);
      return false;
    }
    return true;
  }

  @Override
  public boolean removeByAppId(String appId) {
    Query query = Query.query(Criteria.where(Fields.appId).is(appId));
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonTransformCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public ComparisonTransformConfiguration queryById(String id) {
    Query query = Query.query(Criteria.where(DASH_ID).is(id));
    ConfigComparisonTransformCollection dao =
        mongoTemplate.findOne(query, ConfigComparisonTransformCollection.class);
    return ConfigComparisonTransformMapper.INSTANCE.dtoFromDao(dao);
  }

  public List<ComparisonTransformConfiguration> queryConfigOfCategory(
      String appId, String operationId, List<String> dependencyIds) {
    Query query = new Query();

    List<Criteria> globalCriteriaList = Arrays.asList(
        Criteria.where(Fields.appId).is(appId),
        Criteria.where(Fields.compareConfigType).is(CompareConfigType.REPLAY_MAIN.getCodeValue()),
        Criteria.where(Fields.operationId).is(null)
    );

    List<Criteria> criteriaList = null;
    if (operationId != null) {
      criteriaList = Arrays.asList(
          Criteria.where(Fields.appId).is(appId),
          Criteria.where(Fields.compareConfigType).is(CompareConfigType.REPLAY_MAIN.getCodeValue()),
          Criteria.where(Fields.operationId).is(operationId),
          Criteria.where(Fields.dependencyId).is(null)
      );
    } else {
      if (CollectionUtils.isNotEmpty(dependencyIds)) {
        criteriaList = Arrays.asList(
            Criteria.where(Fields.appId).is(appId),
            Criteria.where(Fields.compareConfigType)
                .is(CompareConfigType.REPLAY_MAIN.getCodeValue()),
            Criteria.where(Fields.dependencyId).in(dependencyIds)
        );
      }
    }

    if (criteriaList != null) {
      query.addCriteria(new Criteria().orOperator(
          new Criteria().andOperator(globalCriteriaList),
          new Criteria().andOperator(criteriaList)
      ));
    } else {
      query.addCriteria(new Criteria().andOperator(globalCriteriaList));
    }
    List<ConfigComparisonTransformCollection> daos =
        mongoTemplate.find(query, ConfigComparisonTransformCollection.class);
    return daos.stream().map(ConfigComparisonTransformMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }
}