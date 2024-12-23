package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LogUtils;
import com.arextest.web.common.RegexUtils;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonExclusionsCollection;
import com.arextest.web.model.dao.mongodb.ConfigComparisonInclusionsCollection;
import com.arextest.web.model.dao.mongodb.ConfigComparisonListSortCollection;
import com.arextest.web.model.dao.mongodb.CountResult;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails.Fields;
import com.arextest.web.model.dto.config.PageQueryComparisonDto;
import com.arextest.web.model.dto.config.PageQueryComparisonResultDto;
import com.arextest.web.model.dto.config.PageQueryListSortDto;
import com.arextest.web.model.mapper.ConfigComparisonExclusionsMapper;
import com.arextest.web.model.mapper.ConfigComparisonListSortMapper;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.ArrayToObject;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.ConcatArrays;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.In;
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
public class ComparisonListSortConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonListSortConfiguration> {

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public List<ComparisonListSortConfiguration> list() {
    throw new UnsupportedOperationException("this method is not implemented");
  }

  @Override
  public List<ComparisonListSortConfiguration> listBy(String appId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId));
    List<ConfigComparisonListSortCollection> configComparisonListSortCollections =
        mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
    return configComparisonListSortCollections.stream()
        .map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  public List<ComparisonListSortConfiguration> listBy(String appId, String operationId) {
    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId).is(appId)
        .and(AbstractComparisonDetails.Fields.operationId).is(operationId).and(
            AbstractComparisonDetails.Fields.compareConfigType)
        .is(CompareConfigType.REPLAY_MAIN.getCodeValue()));
    List<ConfigComparisonListSortCollection> configComparisonListSortCollections =
        mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
    return configComparisonListSortCollections.stream()
        .map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComparisonListSortConfiguration> queryByInterfaceIdAndOperationId(String interfaceId,
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
    List<ConfigComparisonListSortCollection> configComparisonListSortCollections =
        mongoTemplate.find(query, ConfigComparisonListSortCollection.class);
    return configComparisonListSortCollections.stream()
        .map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean update(ComparisonListSortConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    Update update = MongoHelper.getConfigUpdate();
    MongoHelper.appendSpecifiedProperties(update, configuration,
        ConfigComparisonListSortCollection.Fields.listPath,
        ConfigComparisonListSortCollection.Fields.keys,
        AbstractComparisonDetails.Fields.expirationType,
        AbstractComparisonDetails.Fields.expirationDate);
    UpdateResult updateResult = mongoTemplate.updateMulti(query, update,
        ConfigComparisonListSortCollection.class);
    return updateResult.getModifiedCount() > 0;
  }

  @Override
  public boolean remove(ComparisonListSortConfiguration configuration) {
    Query query = Query.query(Criteria.where(DASH_ID).is(configuration.getId()));
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonListSortCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public boolean insert(ComparisonListSortConfiguration configuration) {
    ConfigComparisonListSortCollection configComparisonListSortCollection =
        ConfigComparisonListSortMapper.INSTANCE.daoFromDto(configuration);

    Update update = new Update();
    MongoHelper.appendFullProperties(update, configComparisonListSortCollection);

    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
        .is(configComparisonListSortCollection.getAppId())
        .and(AbstractComparisonDetails.Fields.operationId)
        .is(configComparisonListSortCollection.getOperationId())
        .and(AbstractComparisonDetails.Fields.compareConfigType)
        .is(configComparisonListSortCollection.getCompareConfigType())
        .and(AbstractComparisonDetails.Fields.fsInterfaceId)
        .is(configComparisonListSortCollection.getFsInterfaceId())
        .and(AbstractComparisonDetails.Fields.dependencyId)
        .is(configComparisonListSortCollection.getDependencyId())
        .and(ConfigComparisonListSortCollection.Fields.listPath)
        .is(configComparisonListSortCollection.getListPath()));

    ConfigComparisonListSortCollection dao = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(true).upsert(true),
        ConfigComparisonListSortCollection.class);
    return dao != null;
  }

  @Override
  public boolean insertList(List<ComparisonListSortConfiguration> configurationList) {
    if (CollectionUtils.isEmpty(configurationList)) {
      return false;
    }
    List<ConfigComparisonListSortCollection> listSortCollections = configurationList.stream()
        .map(ConfigComparisonListSortMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
    try {
      BulkOperations bulkOperations =
          mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
              ConfigComparisonListSortCollection.class);
      for (ConfigComparisonListSortCollection listSortCollection : listSortCollections) {
        Update update = new Update();
        MongoHelper.appendFullProperties(update, listSortCollection);

        Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
            .is(listSortCollection.getAppId()).and(AbstractComparisonDetails.Fields.operationId)
            .is(listSortCollection.getOperationId())
            .and(AbstractComparisonDetails.Fields.compareConfigType)
            .is(listSortCollection.getCompareConfigType())
            .and(AbstractComparisonDetails.Fields.fsInterfaceId)
            .is(listSortCollection.getFsInterfaceId())
            .and(AbstractComparisonDetails.Fields.dependencyId)
            .is(listSortCollection.getDependencyId())
            .and(ConfigComparisonListSortCollection.Fields.listPath)
            .is(listSortCollection.getListPath()));
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
    DeleteResult remove = mongoTemplate.remove(query, ConfigComparisonListSortCollection.class);
    return remove.getDeletedCount() > 0;
  }

  @Override
  public ComparisonListSortConfiguration queryById(String id) {
    Query query = Query.query(Criteria.where(DASH_ID).is(id));
    ConfigComparisonListSortCollection dao = mongoTemplate.findOne(query,
        ConfigComparisonListSortCollection.class);
    return ConfigComparisonListSortMapper.INSTANCE.dtoFromDao(dao);
  }

  public PageQueryComparisonResultDto<ComparisonListSortConfiguration> pageQueryComparisonConfig(
      PageQueryListSortDto pageQueryComparisonDto) {
    PageQueryComparisonResultDto<ComparisonListSortConfiguration> resultDto
        = new PageQueryComparisonResultDto<>();

    AggregationOperation project = Aggregation.project(ConfigComparisonListSortCollection.class)
        .and(ArrayOperators.Reduce.arrayOf(ConfigComparisonListSortCollection.Fields.listPath)
            .withInitialValue("").reduce(StringOperators.Concat.valueOf("$$value")
                .concat("/").concatValueOf("$$this"))).as("listPathStr")
        .and(ArrayOperators.Reduce.arrayOf(ConfigComparisonListSortCollection.Fields.keys)
            .withInitialValue(new ArrayList<>()).reduce(ConcatArrays.arrayOf("$$value")
                .concat("$$this"))).as("keyStr");

    Criteria criteria = Criteria.where(Fields.appId).is(pageQueryComparisonDto.getAppId());
    criteria.and(Fields.compareConfigType)
        .is(CompareConfigType.REPLAY_MAIN.getCodeValue());
    if (CollectionUtils.isNotEmpty(pageQueryComparisonDto.getOperationIds())) {
      criteria.and(Fields.operationId).in(pageQueryComparisonDto.getOperationIds());
    }
    if (CollectionUtils.isNotEmpty(pageQueryComparisonDto.getDependencyIds())) {
      criteria.and(Fields.dependencyId).in(pageQueryComparisonDto.getDependencyIds());
    }
    if (StringUtils.isNotEmpty(pageQueryComparisonDto.getKeyOfListPath())) {
      criteria.and("listPathStr")
          .regex(RegexUtils.getRegexForFuzzySearch(pageQueryComparisonDto.getKeyOfListPath()), "i");
    }
    if (StringUtils.isNotEmpty(pageQueryComparisonDto.getKeyOfValue())) {
      criteria.and("keyStr")
          .regex(RegexUtils.getRegexForFuzzySearch(pageQueryComparisonDto.getKeyOfValue()), "i");
    }
    AggregationOperation match = Aggregation.match(criteria);

    if (Objects.equals(pageQueryComparisonDto.getNeedTotal(), true)) {
      AggregationOperation count = Aggregation.count().as(CountResult.Fields.total);
      Aggregation aggCount = Aggregation.newAggregation(project, match, count);
      AggregationResults<CountResult> countResults = mongoTemplate.aggregate(aggCount,
          ConfigComparisonListSortCollection.class, CountResult.class);
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
    AggregationResults<ConfigComparisonListSortCollection> results = mongoTemplate.aggregate(agg,
        ConfigComparisonListSortCollection.class,
        ConfigComparisonListSortCollection.class);

    List<ComparisonListSortConfiguration> configs = results.getMappedResults()
        .stream()
        .map(ConfigComparisonListSortMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
    resultDto.setConfigs(configs);
    return resultDto;
  }
}