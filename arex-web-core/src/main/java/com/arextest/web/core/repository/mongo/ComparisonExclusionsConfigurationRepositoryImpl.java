package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LogUtils;
import com.arextest.web.common.RegexUtils;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonExclusionsCollection;
import com.arextest.web.model.dao.mongodb.CountResult;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails.Fields;
import com.arextest.web.model.dto.config.PageQueryComparisonResultDto;
import com.arextest.web.model.dto.config.PageQueryExclusionDto;
import com.arextest.web.model.mapper.ConfigComparisonExclusionsMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.Arrays;
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
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Slf4j
@Repository
public class ComparisonExclusionsConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonExclusionsConfiguration> {

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public List<ComparisonExclusionsConfiguration> list() {
    throw new UnsupportedOperationException("this method is not implemented");
  }

  @Override
  public List<ComparisonExclusionsConfiguration> listBy(String appId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
    List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections =
        mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
    return configComparisonExclusionsCollections.stream()
        .map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonExclusionsConfiguration> listBy(String appId, String operationId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId)
        .and(AbstractComparisonDetails.Fields.operationId).is(operationId).and(
            AbstractComparisonDetails.Fields.compareConfigType)
        .is(CompareConfigType.REPLAY_MAIN.getCodeValue()));
    List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections =
        mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
    return configComparisonExclusionsCollections.stream()
        .map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonExclusionsConfiguration> queryByInterfaceIdAndOperationId(
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
    List<ConfigComparisonExclusionsCollection> configComparisonExclusionsCollections =
        mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
    return configComparisonExclusionsCollections.stream()
        .map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean update(ComparisonExclusionsConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    Update update = MongoHelper.getConfigUpdate();
    MongoHelper.appendSpecifiedProperties(update, configuration,
        ConfigComparisonExclusionsCollection.Fields.exclusions,
        AbstractComparisonDetails.Fields.expirationType,
        AbstractComparisonDetails.Fields.expirationDate);
    UpdateResult updateResult =
        mongoTemplate.updateMulti(query, update, ConfigComparisonExclusionsCollection.class);
    return updateResult.getModifiedCount() > 0;
  }

  @Override
  public boolean remove(ComparisonExclusionsConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonExclusionsCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public boolean insert(ComparisonExclusionsConfiguration configuration) {
    ConfigComparisonExclusionsCollection configComparisonExclusionsCollection =
        ConfigComparisonExclusionsMapper.INSTANCE.daoFromDto(configuration);

    Update update = new Update();
    MongoHelper.appendFullProperties(update, configComparisonExclusionsCollection);

    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
        .is(configComparisonExclusionsCollection.getAppId())
        .and(AbstractComparisonDetails.Fields.operationId)
        .is(configComparisonExclusionsCollection.getOperationId())
        .and(AbstractComparisonDetails.Fields.compareConfigType)
        .is(configComparisonExclusionsCollection.getCompareConfigType())
        .and(AbstractComparisonDetails.Fields.fsInterfaceId)
        .is(configComparisonExclusionsCollection.getFsInterfaceId())
        .and(AbstractComparisonDetails.Fields.dependencyId)
        .is(configComparisonExclusionsCollection.getDependencyId())
        .and(ConfigComparisonExclusionsCollection.Fields.exclusions)
        .is(configComparisonExclusionsCollection.getExclusions()));

    ConfigComparisonExclusionsCollection dao = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(true).upsert(true),
        ConfigComparisonExclusionsCollection.class);
    return dao != null;
  }

  @Override
  public boolean insertList(List<ComparisonExclusionsConfiguration> configurationList) {
    if (CollectionUtils.isEmpty(configurationList)) {
      return false;
    }
    List<ConfigComparisonExclusionsCollection> exclusionsCollections = configurationList.stream()
        .map(ConfigComparisonExclusionsMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
    try {
      BulkOperations bulkOperations =
          mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
              ConfigComparisonExclusionsCollection.class);
      for (ConfigComparisonExclusionsCollection exclusionsCollection : exclusionsCollections) {
        Update update = new Update();
        MongoHelper.appendFullProperties(update, exclusionsCollection);

        Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
            .is(exclusionsCollection.getAppId()).and(AbstractComparisonDetails.Fields.operationId)
            .is(exclusionsCollection.getOperationId())
            .and(AbstractComparisonDetails.Fields.compareConfigType)
            .is(exclusionsCollection.getCompareConfigType())
            .and(AbstractComparisonDetails.Fields.fsInterfaceId)
            .is(exclusionsCollection.getFsInterfaceId())
            .and(AbstractComparisonDetails.Fields.dependencyId)
            .is(exclusionsCollection.getDependencyId())
            .and(ConfigComparisonExclusionsCollection.Fields.exclusions)
            .is(exclusionsCollection.getExclusions()));
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
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonExclusionsCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public ComparisonExclusionsConfiguration queryById(String id) {
    Query query = Query.query(Criteria.where(DASH_ID).is(id));
    ConfigComparisonExclusionsCollection dao =
        mongoTemplate.findOne(query, ConfigComparisonExclusionsCollection.class);
    return ConfigComparisonExclusionsMapper.INSTANCE.dtoFromDao(dao);
  }

  public List<ComparisonExclusionsConfiguration> queryConfigOfCategory(
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
    List<ConfigComparisonExclusionsCollection> daos =
        mongoTemplate.find(query, ConfigComparisonExclusionsCollection.class);
    return daos.stream().map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  public PageQueryComparisonResultDto<ComparisonExclusionsConfiguration> pageQueryComparisonConfig(
      PageQueryExclusionDto pageQueryComparisonDto) {
    PageQueryComparisonResultDto<ComparisonExclusionsConfiguration> resultDto
        = new PageQueryComparisonResultDto<>();

    AggregationOperation project = Aggregation.project(ComparisonExclusionsConfiguration.class).and(
        ArrayOperators.Reduce.arrayOf(ConfigComparisonExclusionsCollection.Fields.exclusions)
            .withInitialValue("").reduce(StringOperators.Concat.valueOf("$$value")
                .concat("/").concatValueOf("$$this"))).as("exclusionPath");

    Criteria criteria = Criteria.where(Fields.appId).is(pageQueryComparisonDto.getAppId());
    criteria.and(Fields.compareConfigType)
        .is(CompareConfigType.REPLAY_MAIN.getCodeValue());
    if (CollectionUtils.isNotEmpty(pageQueryComparisonDto.getOperationIds())) {
      criteria.and(Fields.operationId).in(pageQueryComparisonDto.getOperationIds());
    }
    if (CollectionUtils.isNotEmpty(pageQueryComparisonDto.getDependencyIds())) {
      criteria.and(Fields.dependencyId).in(pageQueryComparisonDto.getDependencyIds());
    }
    if (StringUtils.isNotEmpty(pageQueryComparisonDto.getKeyOfExclusionPath())) {
      criteria.and("exclusionPath")
          .regex(RegexUtils.getRegexForFuzzySearch(pageQueryComparisonDto.getKeyOfExclusionPath()), "i");
    }
    AggregationOperation match = Aggregation.match(criteria);

    if (Objects.equals(pageQueryComparisonDto.getNeedTotal(), true)) {
      AggregationOperation count = Aggregation.count().as(CountResult.Fields.total);
      Aggregation aggCount = Aggregation.newAggregation(project, match, count);
      AggregationResults<CountResult> countResults = mongoTemplate.aggregate(aggCount,
          ConfigComparisonExclusionsCollection.class, CountResult.class);
      resultDto.setTotalCount(countResults.getUniqueMappedResult() == null ? 0L
          : countResults.getUniqueMappedResult().getTotal());
    }
    Integer pageSize = pageQueryComparisonDto.getPageSize();
    Integer pageIndex = pageQueryComparisonDto.getPageIndex();
    AggregationOperation skip = Aggregation.skip((long) (pageIndex - 1) * pageSize);
    AggregationOperation limit = Aggregation.limit(pageSize);
    AggregationOperation sort = Aggregation.sort(Sort.by(Sort.Direction.ASC, Fields.operationId)
        .and(Sort.by(Sort.Direction.ASC, Fields.dependencyId)));

    Aggregation agg = Aggregation.newAggregation(project, match, sort, skip, limit);
    AggregationResults<ConfigComparisonExclusionsCollection> results = mongoTemplate.aggregate(agg,
        ConfigComparisonExclusionsCollection.class,
        ConfigComparisonExclusionsCollection.class);

    List<ComparisonExclusionsConfiguration> configs = results.getMappedResults()
        .stream()
        .map(ConfigComparisonExclusionsMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
    resultDto.setConfigs(configs);
    return resultDto;
  }
}