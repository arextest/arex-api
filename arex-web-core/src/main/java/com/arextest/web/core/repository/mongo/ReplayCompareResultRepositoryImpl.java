package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.model.contract.contracts.QueryReplayCaseRequestType;
import com.arextest.web.model.dao.mongodb.ReplayCompareResultCollection;
import com.arextest.web.model.dao.mongodb.ReplayCompareResultCollection.Fields;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.mapper.CompareResultMapper;
import com.mongodb.client.result.DeleteResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.bson.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Slf4j
@Component
public class ReplayCompareResultRepositoryImpl implements ReplayCompareResultRepository {

  private static final String PLAN_ID = "planId";
  private static final String BASE_MSG = "baseMsg";
  private static final String TEST_MSG = "testMsg";
  private static final String PLAN_ITEM_ID = "planItemId";
  private static final String OPERATION_ID = "operationId";
  private static final String CATEGORY_NAME = "categoryName";
  private static final String OPERATION_NAME = "operationName";
  private static final String REPLAY_ID = "replayId";
  private static final String RECORD_ID = "recordId";
  private static final String DIFF_RESULT_CODE = "diffResultCode";
  private static final String LOGS = "logs";
  private static final String COUNT = "count";
  private static final String RECORD_TIME = "recordTime";
  private static final String REPLAY_TIME = "replayTime";

  private static final String DASH_ID = "_id";
  private static final String MAX_CREATE_DATE = "maxCreateDate";
  private static final String MAX_DIFF_CODE = "maxDiffCode";

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public boolean updateResults(List<CompareResultDto> results) {
    try {
      BulkOperations bulkOperations =
          mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
              ReplayCompareResultCollection.class);
      List<org.springframework.data.util.Pair<Query, UpdateDefinition>> updates = new ArrayList<>();
      for (CompareResultDto compareResultDto : results) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DASH_ID).is(compareResultDto.getId()));
        Update update = new Update();
        update.set(DIFF_RESULT_CODE, compareResultDto.getDiffResultCode());
        updates.add(org.springframework.data.util.Pair.of(query, update));
      }
      bulkOperations.updateMulti(updates);
      bulkOperations.execute();
    } catch (Exception e) {
      LogUtils.error(LOGGER, "updateResults failed! list:{}", results, e);
      return false;
    }
    return true;
  }

  @Override
  public Long countWithDistinct(String planItemId, Integer diffResultCode, String keyword) {
    Criteria criteria = Criteria.where(Fields.planItemId).is(planItemId);
    if (Strings.isNotBlank(keyword)) {
      criteria.andOperator(new Criteria().orOperator(Criteria.where(Fields.recordId).is(keyword),
          Criteria.where(Fields.replayId).is(keyword)));
    }

    if (diffResultCode == null) {
      Query query = Query.query(criteria);

      List<String> result = mongoTemplate.findDistinct(query, Fields.caseId, ReplayCompareResultCollection.class, String.class);

      return (long) result.size();
    }

    Aggregation aggregation = Aggregation.newAggregation(
        TypedAggregation.match(criteria),
        Aggregation.project(Fields.caseId, Fields.diffResultCode),
        Aggregation.group(Fields.caseId).max(Fields.diffResultCode).as(MAX_DIFF_CODE),
        Aggregation.match(Criteria.where(MAX_DIFF_CODE).is(diffResultCode)),
        Aggregation.count().as(COUNT)
    );

    AggregationResults<Document> aggregationResults = mongoTemplate.aggregate(aggregation, ReplayCompareResultCollection.class, Document.class);

    if (CollectionUtils.isEmpty(aggregationResults.getMappedResults())) {
        return 0L;
    }
    return (long) aggregationResults.getMappedResults().get(0).getInteger(COUNT, 0);
  }

  @Override
  public List<CompareResultDto> findResultWithoutMsg(String planItemId) {
    if (StringUtils.isEmpty(planItemId)) {
      return Collections.emptyList();
    }
    Query query = fillFilterConditions(null, planItemId, null, null, null);
    query.fields().exclude(BASE_MSG).exclude(TEST_MSG).exclude(LOGS);

    List<ReplayCompareResultCollection> results = mongoTemplate.find(query,
        ReplayCompareResultCollection.class);
    return results.stream().map(CompareResultMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public List<CompareResultDto> findResultWithoutMsg(QueryReplayCaseRequestType request) {
    Criteria criteria = Criteria.where(Fields.planItemId).is(request.getPlanItemId());
    if (Strings.isNotBlank(request.getKeyWord())) {
      criteria.andOperator(new Criteria().orOperator(Criteria.where(Fields.recordId).is(request.getKeyWord()),
          Criteria.where(Fields.replayId).is(request.getKeyWord())));
    }
    int limit = 5;
    if (request.getPageSize() != null) {
      limit = Integer.min(request.getPageSize(), limit);
    }
    int skip = 0;
    if (request.getPageIndex() != null && request.getPageIndex() > 0) {
      skip = (request.getPageIndex() - 1) * limit;
    }

    Criteria diffCodeCriteria = new Criteria();
    if (request.getDiffResultCode() != null) {
      diffCodeCriteria.and(MAX_DIFF_CODE).is(request.getDiffResultCode());
    }

    Aggregation aggregation = Aggregation.newAggregation(
        TypedAggregation.match(criteria),
        Aggregation.project(DASH_ID, Fields.caseId, Fields.planItemId, Fields.diffResultCode, Fields.recordId,
            Fields.replayId, Fields.dataChangeCreateDate),
        Aggregation.group(Fields.caseId, Fields.recordId, Fields.replayId)
            .max(Fields.dataChangeCreateDate).as(MAX_CREATE_DATE)
            .max(Fields.diffResultCode).as(MAX_DIFF_CODE),
        Aggregation.match(diffCodeCriteria),
        Aggregation.sort(Direction.DESC, MAX_DIFF_CODE, MAX_CREATE_DATE),
        Aggregation.skip(skip),
        Aggregation.limit(limit)
    );

    AggregationResults<Document> aggregationResults = mongoTemplate.aggregate(aggregation, ReplayCompareResultCollection.class, Document.class);
    List<Document> aggregationDocumentList = aggregationResults.getMappedResults();
    if (CollectionUtils.isEmpty(aggregationDocumentList)) {
      return Collections.emptyList();
    }

    List<CompareResultDto> compareResultList = new ArrayList<>(aggregationDocumentList.size());
    for (Document document : aggregationDocumentList) {
        CompareResultDto compareResultDto = new CompareResultDto();
        Document idDoc = (Document) document.get(DASH_ID);
        compareResultDto.setCaseId(idDoc.getString(Fields.caseId));
        compareResultDto.setRecordId(idDoc.getString(Fields.recordId));
        compareResultDto.setReplayId(idDoc.getString(Fields.replayId));
        compareResultDto.setDiffResultCode(document.getInteger(MAX_DIFF_CODE));

        compareResultList.add(compareResultDto);
    }

    return compareResultList;
  }

  @Override
  public Pair<List<CompareResultDto>, Long> queryCompareResultByPage(String planId,
      Integer pageSize,
      Integer pageIndex) {
    Query query = Query.query(Criteria.where(PLAN_ID).is(planId));
    query.fields().exclude(BASE_MSG).exclude(TEST_MSG);

    Long totalCount = mongoTemplate.count(query, ReplayCompareResultCollection.class);

    Pageable pageable = PageRequest.of(pageIndex, pageSize);
    query.with(pageable);
    List<ReplayCompareResultCollection> daos = mongoTemplate.find(query,
        ReplayCompareResultCollection.class);
    List<CompareResultDto> dtos =
        daos.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    return new MutablePair<>(dtos, totalCount);
  }

  @Override
  public Pair<List<CompareResultDto>, Long> queryAllDiffMsgByPage(String planItemId,
      String recordId,
      List<Integer> diffResultCodeList, Integer pageSize, Integer pageIndex, Boolean needTotal) {
    Query query = Query.query(
        Criteria.where(PLAN_ITEM_ID).is(planItemId).and(RECORD_ID).is(recordId));

    if (CollectionUtils.isNotEmpty(diffResultCodeList)) {
      query.addCriteria(Criteria.where(DIFF_RESULT_CODE).in(diffResultCodeList));
    }
    Long totalCount = -1L;
    if (needTotal) {
      totalCount = mongoTemplate.count(query, ReplayCompareResultCollection.class);
    }
    Pageable pageable = PageRequest.of(pageIndex, pageSize);
    query.with(pageable);
    query.with(Sort.by(Sort.Direction.ASC, REPLAY_TIME));
    query.with(Sort.by(Sort.Direction.ASC, RECORD_TIME));
    List<ReplayCompareResultCollection> result = mongoTemplate.find(query,
        ReplayCompareResultCollection.class);
    List<CompareResultDto> dtos =
        result.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());

    return new MutablePair<>(dtos, totalCount);
  }

  @Override
  public CompareResultDto queryCompareResultsById(String objectId) {
    Query query = new Query();
    query.addCriteria(Criteria.where(DASH_ID).is(objectId));
    ReplayCompareResultCollection result = mongoTemplate.findOne(query,
        ReplayCompareResultCollection.class);
    return CompareResultMapper.INSTANCE.dtoFromDao(result);
  }

  @Override
  public List<CompareResultDto> queryCompareResultsByRecordId(String planItemId, String recordId) {
    Query query = new Query();
    query.addCriteria(Criteria.where(RECORD_ID).is(recordId).and(PLAN_ITEM_ID).is(planItemId));
    List<ReplayCompareResultCollection> daos = mongoTemplate.find(query,
        ReplayCompareResultCollection.class);
    return daos.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }

  @Override
  public boolean deleteCompareResultsByPlanId(String planId) {
    Query query = Query.query(Criteria.where(PLAN_ID).is(planId));
    DeleteResult deleteResult = mongoTemplate.remove(query, ReplayCompareResultCollection.class);
    return deleteResult.getDeletedCount() > 0;
  }

  @Override
  public int queryCompareResultCountByPlanId(String planId) {
    Query query = Query.query(Criteria.where(PLAN_ID).is(planId));
    return mongoTemplate.findDistinct(query, RECORD_ID, ReplayCompareResultCollection.class,
        String.class).size();
  }

  @Override
  public List<CompareResultDto> queryCompareResults(String planId, List<String> planItemIdList,
      List<String> recordIdList, List<Integer> diffResultCodeList, List<String> showFields) {
    Query query = Query.query(Criteria.where(PLAN_ID).is(planId));
    if (CollectionUtils.isNotEmpty(planItemIdList)) {
      query.addCriteria(Criteria.where(PLAN_ITEM_ID).in(planItemIdList));
    }
    if (CollectionUtils.isNotEmpty(recordIdList)) {
      query.addCriteria(Criteria.where(RECORD_ID).in(recordIdList));
    }
    if (CollectionUtils.isNotEmpty(diffResultCodeList)) {
      query.addCriteria(Criteria.where(DIFF_RESULT_CODE).in(diffResultCodeList));
    }
    if (CollectionUtils.isNotEmpty(showFields)) {
      for (String showField : showFields) {
        query.fields().include(showField);
      }
    }

    List<ReplayCompareResultCollection> daos = mongoTemplate.find(query,
        ReplayCompareResultCollection.class);
    return daos.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());

  }

  @Override
  public List<CompareResultDto> queryCompareResults(List<String> planItemIdList,
      List<String> recordIdList) {
    Query query = new Query();
    Criteria criteria = new Criteria();
    List<Criteria> orCriteriaList = new ArrayList<>();
    for (int i = 0; i < planItemIdList.size(); i++) {
      orCriteriaList
          .add(Criteria.where(PLAN_ITEM_ID).is(planItemIdList.get(i)).and(RECORD_ID)
              .is(recordIdList.get(i)));
    }
    criteria.orOperator(orCriteriaList);
    query.addCriteria(criteria);
    List<ReplayCompareResultCollection> daos = mongoTemplate.find(query,
        ReplayCompareResultCollection.class);
    return daos.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }

  @Override
  public List<CompareResultDto> queryLatestCompareResultByType(String operationId,
                                                               Set<String> operationTypes,
                                                               int limit) {
    Sort sort = Sort.by(Sort.Direction.DESC, DATA_CHANGE_UPDATE_TIME);
    List<ReplayCompareResultCollection> collections = new ArrayList<>();

    for (String operationType : operationTypes) {
      Query query = Query.query(Criteria.where(OPERATION_ID).is(operationId)
          .and(CATEGORY_NAME).is(operationType)).with(sort).limit(limit);
      collections.addAll(mongoTemplate.find(query, ReplayCompareResultCollection.class));
    }
    return collections.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }

  @Override
  public List<CompareResultDto> queryLatestCompareResultForEachType(String operationId, int limit) {

    List<String> categoryNames = mongoTemplate.findDistinct(Query.query(Criteria.where(OPERATION_ID).is(operationId)),
        CATEGORY_NAME, ReplayCompareResultCollection.class, String.class);
    return queryLatestCompareResultByType(operationId, new HashSet<>(categoryNames), limit);
  }

  @Override
  public Map<String, List<CompareResultDto>> queryLatestCompareResultMap(String operationId,
                                                                         List<String> operationNames, List<String> operationTypes) {
    Criteria criteria = new Criteria();
    List<Criteria> orCriteriaList = new ArrayList<>();
    for (int i = 0; i < operationNames.size(); i++) {
      orCriteriaList.add(
          Criteria.where(OPERATION_NAME).is(operationNames.get(i)).and(CATEGORY_NAME)
              .is(operationTypes.get(i)));
    }
    criteria.orOperator(orCriteriaList).andOperator(Criteria.where(OPERATION_ID).is(operationId));

    Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, DATA_CHANGE_UPDATE_TIME));
    return mongoTemplate.find(query, ReplayCompareResultCollection.class).stream()
        .map(CompareResultMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.groupingBy(CompareResultDto::getOperationName));
  }

  private Query fillFilterConditions(String planId, String planItemId, String categoryName,
      Integer resultType,
      String keyWord) {
    Query query = new Query();
    if (planId != null) {
      query.addCriteria(Criteria.where(PLAN_ID).is(planId));
    }
    if (planItemId != null) {
      query.addCriteria(Criteria.where(PLAN_ITEM_ID).is(planItemId));
    }
    if (categoryName != null) {
      query.addCriteria(Criteria.where(CATEGORY_NAME).is(categoryName));
    }
    if (resultType != null) {
      query.addCriteria(Criteria.where(DIFF_RESULT_CODE).is(resultType));
    }
    if (Strings.isNotBlank(keyWord)) {
      query.addCriteria(
          new Criteria().orOperator(Criteria.where(REPLAY_ID).regex(".*?" + keyWord + ".*"),
              Criteria.where(RECORD_ID).regex(".*?" + keyWord + ".*")));
    }
    return query;
  }
}
