package com.arextest.web.core.repository.mongo;

import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.RegexUtils;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.common.enums.CompareConfigType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.dao.mongodb.ConfigComparisonIgnoreCategoryCollection;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails;
import com.arextest.web.model.dao.mongodb.entity.AbstractComparisonDetails.Fields;
import com.arextest.web.model.dao.mongodb.entity.CategoryDetailDao;
import com.arextest.web.model.dto.config.PageQueryCategoryDto;
import com.arextest.web.model.dto.config.PageQueryComparisonDto;
import com.arextest.web.model.dto.config.PageQueryComparisonResultDto;
import com.arextest.web.model.mapper.ConfigComparisonIgnoreCategoryMapper;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author wildeslam.
 * @create 2023/8/18 15:02
 */
@Slf4j
@Repository
public class ComparisonIgnoreCategoryConfigurationRepositoryImpl
    implements ConfigRepositoryProvider<ComparisonIgnoreCategoryConfiguration> {

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

    Update update = MongoHelper.getUpdate();
    MongoHelper.appendFullProperties(update, configuration);

    Query query = Query.query(Criteria.where(AbstractComparisonDetails.Fields.appId)
        .is(collection.getAppId())
        .and(AbstractComparisonDetails.Fields.operationId)
        .is(collection.getOperationId())
        .and(AbstractComparisonDetails.Fields.compareConfigType)
        .is(collection.getCompareConfigType())
        .and(AbstractComparisonDetails.Fields.fsInterfaceId)
        .is(collection.getFsInterfaceId())
        .and(AbstractComparisonDetails.Fields.dependencyId)
        .is(collection.getDependencyId())
        .and(ConfigComparisonIgnoreCategoryCollection.Fields.ignoreCategoryDetail)
        .is(collection.getIgnoreCategoryDetail()));

    ConfigComparisonIgnoreCategoryCollection dao = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().returnNew(true).upsert(true),
        ConfigComparisonIgnoreCategoryCollection.class);
    return dao != null;
  }

  public PageQueryComparisonResultDto<ComparisonIgnoreCategoryConfiguration> pageQueryComparisonConfig(
      PageQueryCategoryDto pageQueryComparisonDto) {
    PageQueryComparisonResultDto<ComparisonIgnoreCategoryConfiguration> resultDto
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
    if (StringUtils.isNotEmpty(pageQueryComparisonDto.getKeyOfIgnoreOperationType())) {
      query.addCriteria(Criteria.where(
              ConfigComparisonIgnoreCategoryCollection.Fields.ignoreCategoryDetail.concat(".").concat(
                  CategoryDetailDao.Fields.operationType))
          .regex(RegexUtils.getRegexForFuzzySearch(pageQueryComparisonDto.getKeyOfIgnoreOperationType()), "i"));
    }
    if (StringUtils.isNotEmpty(pageQueryComparisonDto.getKeyOfIgnoreOperationName())){
      query.addCriteria(Criteria.where(
              ConfigComparisonIgnoreCategoryCollection.Fields.ignoreCategoryDetail.concat(".").concat(
                  CategoryDetailDao.Fields.operationName))
          .regex(RegexUtils.getRegexForFuzzySearch(pageQueryComparisonDto.getKeyOfIgnoreOperationName()), "i"));
    }
    if (Objects.equals(pageQueryComparisonDto.getNeedTotal(), true)) {
      resultDto.setTotalCount(
          mongoTemplate.count(query, ConfigComparisonIgnoreCategoryCollection.class)
      );
    }
    Integer pageSize = pageQueryComparisonDto.getPageSize();
    Integer pageIndex = pageQueryComparisonDto.getPageIndex();
    query.skip((long) (pageIndex - 1) * pageSize).limit(pageSize);

    query.with(
        Sort.by(Sort.Direction.ASC, Fields.operationId)
            .and(Sort.by(Sort.Direction.ASC, Fields.dependencyId))
    );
    List<ComparisonIgnoreCategoryConfiguration> configs = mongoTemplate.find(query,
            ConfigComparisonIgnoreCategoryCollection.class)
        .stream()
        .map(ConfigComparisonIgnoreCategoryMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
    resultDto.setConfigs(configs);
    return resultDto;
  }
}
