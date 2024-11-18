package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonScriptCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails.Fields;
import com.arextest.web.model.dto.config.PageQueryComparisonDto;
import com.arextest.web.model.dto.config.PageQueryComparisonResultDto;
import com.arextest.web.model.mapper.ConfigComparisonScriptMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ComparisonScriptConfigurationRepositoryImpl implements
    ConfigRepositoryProvider<ComparisonScriptConfiguration> {

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public List<ComparisonScriptConfiguration> list() {
    throw new UnsupportedOperationException("this method is not implemented");
  }

  @Override
  public List<ComparisonScriptConfiguration> listBy(String appId) {
    Query query = Query.query(Criteria.where(Fields.appId).is(appId));
    List<ConfigComparisonScriptCollection> daos =
        mongoTemplate.find(query, ConfigComparisonScriptCollection.class);
    return daos.stream()
        .map(ConfigComparisonScriptMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonScriptConfiguration> listBy(String appId, String operationId) {
    Query query = Query.query(
        Criteria.where(Fields.appId).is(appId)
            .and(Fields.operationId).is(operationId)
            .and(Fields.compareConfigType).is(CompareConfigType.REPLAY_MAIN.getCodeValue())
    );
    List<ConfigComparisonScriptCollection> daos =
        mongoTemplate.find(query, ConfigComparisonScriptCollection.class);
    return daos.stream()
        .map(ConfigComparisonScriptMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonScriptConfiguration> queryByInterfaceIdAndOperationId(
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
    List<ConfigComparisonScriptCollection> daos =
        mongoTemplate.find(query, ConfigComparisonScriptCollection.class);
    return daos.stream()
        .map(ConfigComparisonScriptMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean update(ComparisonScriptConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    Update update = MongoHelper.getConfigUpdate();
    MongoHelper.appendSpecifiedProperties(update, configuration,
        ConfigComparisonScriptCollection.Fields.nodePath,
        ConfigComparisonScriptCollection.Fields.scriptMethod,
        Fields.expirationType,
        Fields.expirationDate);
    UpdateResult updateResult =
        mongoTemplate.updateMulti(query, update, ConfigComparisonScriptCollection.class);
    return updateResult.getModifiedCount() > 0;
  }

  @Override
  public boolean remove(ComparisonScriptConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonScriptCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public boolean insert(ComparisonScriptConfiguration configuration) {
    ConfigComparisonScriptCollection configComparisonScriptCollection =
        ConfigComparisonScriptMapper.INSTANCE.daoFromDto(configuration);

    Update update = new Update();
    MongoHelper.appendFullProperties(update, configComparisonScriptCollection);

    Query query = Query.query(Criteria.where(Fields.appId)
        .is(configComparisonScriptCollection.getAppId())
        .and(Fields.operationId)
        .is(configComparisonScriptCollection.getOperationId())
        .and(Fields.compareConfigType)
        .is(configComparisonScriptCollection.getCompareConfigType())
        .and(Fields.fsInterfaceId)
        .is(configComparisonScriptCollection.getFsInterfaceId())
        .and(Fields.dependencyId)
        .is(configComparisonScriptCollection.getDependencyId())
        .and(ConfigComparisonScriptCollection.Fields.nodePath)
        .is(configComparisonScriptCollection.getNodePath())
    );

    ConfigComparisonScriptCollection dao = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(true).upsert(true),
        ConfigComparisonScriptCollection.class);
    return dao != null;
  }

  @Override
  public boolean insertList(List<ComparisonScriptConfiguration> configurationList) {
    if (CollectionUtils.isEmpty(configurationList)) {
      return false;
    }
    List<ConfigComparisonScriptCollection> transformCollections = configurationList.stream()
        .map(ConfigComparisonScriptMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
    try {
      BulkOperations bulkOperations =
          mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
              ConfigComparisonScriptCollection.class);
      for (ConfigComparisonScriptCollection item : transformCollections) {
        Update update = new Update();
        MongoHelper.appendFullProperties(update, item);

        Query query = Query.query(
            Criteria.where(Fields.appId).is(item.getAppId())
                .and(Fields.operationId).is(item.getOperationId())
                .and(Fields.compareConfigType).is(item.getCompareConfigType())
                .and(Fields.fsInterfaceId).is(item.getFsInterfaceId())
                .and(Fields.dependencyId).is(item.getDependencyId())
                .and(ConfigComparisonScriptCollection.Fields.nodePath)
                .is(item.getNodePath())
        );
        bulkOperations.upsert(query, update);
      }
      bulkOperations.execute();
    } catch (Exception e) {
      LogUtils.error(LOGGER, "script insertList failed! list:{}, exception:{}",
          configurationList, e);
      return false;
    }
    return true;
  }

  @Override
  public boolean removeByAppId(String appId) {
    Query query = Query.query(Criteria.where(Fields.appId).is(appId));
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonScriptCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public ComparisonScriptConfiguration queryById(String id) {
    Query query = Query.query(Criteria.where(DASH_ID).is(id));
    ConfigComparisonScriptCollection dao =
        mongoTemplate.findOne(query, ConfigComparisonScriptCollection.class);
    return ConfigComparisonScriptMapper.INSTANCE.dtoFromDao(dao);
  }

  public PageQueryComparisonResultDto<ComparisonScriptConfiguration> pageQueryComparisonConfig(
      PageQueryComparisonDto pageQueryComparisonDto) {
    PageQueryComparisonResultDto<ComparisonScriptConfiguration> resultDto
        = new PageQueryComparisonResultDto<>();

    Query query = new Query();
    query.addCriteria(Criteria.where(Fields.appId).is(pageQueryComparisonDto.getAppId()));
    query.addCriteria(Criteria.where(Fields.compareConfigType)
        .is(CompareConfigType.REPLAY_MAIN.getCodeValue()));
    List<String> operationIds = pageQueryComparisonDto.getOperationIds();
    if (CollectionUtils.isNotEmpty(operationIds)) {
      query.addCriteria(Criteria.where(Fields.operationId).in(operationIds));
    }
    if (CollectionUtils.isNotEmpty(pageQueryComparisonDto.getDependencyIds())) {
      query.addCriteria(Criteria.where(Fields.dependencyId)
          .in(pageQueryComparisonDto.getDependencyIds()));
    }
    if (Objects.equals(pageQueryComparisonDto.getNeedTotal(), true)) {
      resultDto.setTotalCount(
          mongoTemplate.count(query, ConfigComparisonScriptCollection.class)
      );
    }
    Integer pageSize = pageQueryComparisonDto.getPageSize();
    Integer pageIndex = pageQueryComparisonDto.getPageIndex();
    query.skip((long) (pageIndex - 1) * pageSize).limit(pageSize);

    query.with(
        Sort.by(Sort.Direction.ASC, Fields.operationId)
            .and(Sort.by(Sort.Direction.ASC, Fields.dependencyId))
    );
    List<ComparisonScriptConfiguration> configs = mongoTemplate.find(query,
            ConfigComparisonScriptCollection.class)
        .stream()
        .map(ConfigComparisonScriptMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
    resultDto.setConfigs(configs);
    return resultDto;
  }


}
