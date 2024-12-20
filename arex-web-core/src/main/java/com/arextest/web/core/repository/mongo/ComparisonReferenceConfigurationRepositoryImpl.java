package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonReferenceCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails.Fields;
import com.arextest.web.model.dto.config.PageQueryComparisonDto;
import com.arextest.web.model.dto.config.PageQueryComparisonResultDto;
import com.arextest.web.model.mapper.ConfigComparisonReferenceMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
public class ComparisonReferenceConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonReferenceConfiguration> {

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public List<ComparisonReferenceConfiguration> list() {
    throw new UnsupportedOperationException("this method is not implemented");
  }

  @Override
  public List<ComparisonReferenceConfiguration> listBy(String appId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
    List<ConfigComparisonReferenceCollection> configComparisonReferenceCollections =
        mongoTemplate.find(query, ConfigComparisonReferenceCollection.class);
    return configComparisonReferenceCollections.stream()
        .map(ConfigComparisonReferenceMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  public List<ComparisonReferenceConfiguration> listBy(String appId, String operationId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId)
        .and(AbstractComparisonDetails.Fields.operationId).is(operationId).and(
            AbstractComparisonDetails.Fields.compareConfigType)
        .is(CompareConfigType.REPLAY_MAIN.getCodeValue()));
    List<ConfigComparisonReferenceCollection> configComparisonReferenceCollections =
        mongoTemplate.find(query, ConfigComparisonReferenceCollection.class);
    return configComparisonReferenceCollections.stream()
        .map(ConfigComparisonReferenceMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonReferenceConfiguration> queryByInterfaceIdAndOperationId(String interfaceId,
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
    List<ConfigComparisonReferenceCollection> configComparisonReferenceCollections =
        mongoTemplate.find(query, ConfigComparisonReferenceCollection.class);
    return configComparisonReferenceCollections.stream()
        .map(ConfigComparisonReferenceMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean update(ComparisonReferenceConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    Update update = MongoHelper.getConfigUpdate();
    MongoHelper.appendSpecifiedProperties(update, configuration,
        ConfigComparisonReferenceCollection.Fields.pkPath,
        ConfigComparisonReferenceCollection.Fields.fkPath,
        AbstractComparisonDetails.Fields.expirationType,
        AbstractComparisonDetails.Fields.expirationDate);
    UpdateResult updateResult = mongoTemplate.updateMulti(query, update,
        ConfigComparisonReferenceCollection.class);
    return updateResult.getModifiedCount() > 0;
  }

  @Override
  public boolean remove(ComparisonReferenceConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonReferenceCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public boolean insert(ComparisonReferenceConfiguration configuration) {
    ConfigComparisonReferenceCollection configComparisonReferenceCollection =
        ConfigComparisonReferenceMapper.INSTANCE.daoFromDto(configuration);

    Update update = new Update();
    MongoHelper.appendFullProperties(update, configComparisonReferenceCollection);

    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
        .is(configComparisonReferenceCollection.getAppId())
        .and(AbstractComparisonDetails.Fields.operationId)
        .is(configComparisonReferenceCollection.getOperationId())
        .and(AbstractComparisonDetails.Fields.compareConfigType)
        .is(configComparisonReferenceCollection.getCompareConfigType())
        .and(AbstractComparisonDetails.Fields.fsInterfaceId)
        .is(configComparisonReferenceCollection.getFsInterfaceId())
        .and(AbstractComparisonDetails.Fields.dependencyId)
        .is(configComparisonReferenceCollection.getDependencyId())
        .and(ConfigComparisonReferenceCollection.Fields.pkPath)
        .is(configComparisonReferenceCollection.getPkPath())
        .and(ConfigComparisonReferenceCollection.Fields.fkPath)
        .is(configComparisonReferenceCollection.getFkPath()));

    ConfigComparisonReferenceCollection dao = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(true).upsert(true),
        ConfigComparisonReferenceCollection.class);
    return dao != null;
  }

  @Override
  public boolean insertList(List<ComparisonReferenceConfiguration> configurationList) {
    if (CollectionUtils.isEmpty(configurationList)) {
      return false;
    }
    List<ConfigComparisonReferenceCollection> referenceCollections = configurationList.stream()
        .map(ConfigComparisonReferenceMapper.INSTANCE::daoFromDto).collect(Collectors.toList());

    try {
      BulkOperations bulkOperations =
          mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
              ConfigComparisonReferenceCollection.class);
      for (ConfigComparisonReferenceCollection referenceCollection : referenceCollections) {
        Update update = new Update();
        MongoHelper.appendFullProperties(update, referenceCollection);

        Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
            .is(referenceCollection.getAppId()).and(AbstractComparisonDetails.Fields.operationId)
            .is(referenceCollection.getOperationId())
            .and(AbstractComparisonDetails.Fields.compareConfigType)
            .is(referenceCollection.getCompareConfigType())
            .and(AbstractComparisonDetails.Fields.fsInterfaceId)
            .is(referenceCollection.getFsInterfaceId())
            .and(AbstractComparisonDetails.Fields.dependencyId)
            .is(referenceCollection.getDependencyId())
            .and(ConfigComparisonReferenceCollection.Fields.pkPath)
            .is(referenceCollection.getPkPath())
            .and(ConfigComparisonReferenceCollection.Fields.fkPath)
            .is(referenceCollection.getFkPath()));
        bulkOperations.upsert(query, update);
      }
      bulkOperations.execute();
    } catch (Exception e) {
      LogUtils.error(LOGGER, "exclusion insertList failed! list:{}, exception:{}",
          configurationList, e);
      return false;
    }
    return true;
  }

  @Override
  public boolean removeByAppId(String appId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonReferenceCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public ComparisonReferenceConfiguration queryById(String id) {
    Query query = Query.query(Criteria.where(DASH_ID).is(id));
    ConfigComparisonReferenceCollection dao =
        mongoTemplate.findOne(query, ConfigComparisonReferenceCollection.class);
    return ConfigComparisonReferenceMapper.INSTANCE.dtoFromDao(dao);
  }


  public PageQueryComparisonResultDto<ComparisonReferenceConfiguration> pageQueryComparisonConfig(
      PageQueryComparisonDto pageQueryComparisonDto) {
    PageQueryComparisonResultDto<ComparisonReferenceConfiguration> resultDto
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
          mongoTemplate.count(query, ConfigComparisonReferenceCollection.class)
      );
    }
    Integer pageSize = pageQueryComparisonDto.getPageSize();
    Integer pageIndex = pageQueryComparisonDto.getPageIndex();
    query.skip((long) (pageIndex - 1) * pageSize).limit(pageSize);

    query.with(
        Sort.by(Sort.Direction.ASC, Fields.operationId)
            .and(Sort.by(Sort.Direction.ASC, Fields.dependencyId))
    );
    List<ComparisonReferenceConfiguration> configs = mongoTemplate.find(query,
            ConfigComparisonReferenceCollection.class)
        .stream()
        .map(ConfigComparisonReferenceMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
    resultDto.setConfigs(configs);
    return resultDto;
  }
}