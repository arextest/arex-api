package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsRequestType;
import com.arextest.web.model.dao.mongodb.ModelBase;
import com.arextest.web.model.dao.mongodb.ReportPlanStatisticCollection;
import com.arextest.web.model.dao.mongodb.ReportPlanStatisticCollection.Fields;
import com.arextest.web.model.dto.LatestDailySuccessPlanIdDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.enums.ReplayStatusType;
import com.arextest.web.model.mapper.PlanMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.DeleteResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
public class ReportPlanStatisticRepositoryImpl implements ReportPlanStatisticRepository {

  private static final String DATE_TIME = "dateTime";

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public List<ReportPlanStatisticDto> findByDataCreateTimeBetween(Date startTime, Date endTime) {
    Query query = new Query();
    Criteria criteria = Criteria.where(ModelBase.Fields.dataChangeCreateTime).gte(startTime)
        .lte(endTime);
    query.addCriteria(criteria);
    List<ReportPlanStatisticCollection> result = mongoTemplate.find(query,
        ReportPlanStatisticCollection.class);
    return result.stream().map(PlanMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }

  @Override
  public ReportPlanStatisticDto findByPlanId(String planId) {
    if (planId == null) {
      return null;
    }
    ReportPlanStatisticCollection result =
        mongoTemplate.findOne(Query.query(Criteria.where(Fields.planId).is(planId)),
            ReportPlanStatisticCollection.class);
    return PlanMapper.INSTANCE.dtoFromDao(result);
  }

  @Override
  public boolean findAndModifyBaseInfo(ReportPlanStatisticDto result) {
    if (result.getPlanId() == null) {
      return false;
    }

    Update update = MongoHelper.getUpdate();
    update.setOnInsert(ModelBase.Fields.dataChangeCreateTime, System.currentTimeMillis());

    if (result.getCaseStartTime() != null) {
      update.set(Fields.caseStartTime, result.getCaseStartTime());
    }
    if (result.getCaseEndTime() != null) {
      update.set(Fields.caseEndTime, result.getCaseEndTime());
    }
    if (!StringUtils.isEmpty(result.getPlanName())) {
      update.set(Fields.planName, result.getPlanName());
    }
    if (result.getStatus() != null) {
      update.set(Fields.status, result.getStatus());
    }
    if (!StringUtils.isEmpty(result.getAppId())) {
      update.set(Fields.appId, result.getAppId());
    }
    if (!StringUtils.isEmpty(result.getAppName())) {
      update.set(Fields.appName, result.getAppName());
    }
    if (!StringUtils.isEmpty(result.getCreator())) {
      update.set(Fields.creator, result.getCreator());
    }
    if (result.getTargetImageId() != null) {
      update.set(Fields.targetImageId, result.getTargetImageId());
    }
    if (!StringUtils.isEmpty(result.getTargetImageName())) {
      update.set(Fields.targetImageName, result.getTargetImageName());
    }
    if (result.getCaseSourceType() != null) {
      update.set(Fields.caseSourceType, result.getCaseSourceType());
    }
    if (!StringUtils.isEmpty(result.getSourceEnv())) {
      update.set(Fields.sourceEnv, result.getSourceEnv());
    }
    if (!StringUtils.isEmpty(result.getTargetEnv())) {
      update.set(Fields.targetEnv, result.getTargetEnv());
    }
    if (!StringUtils.isEmpty(result.getSourceHost())) {
      update.set(Fields.sourceHost, result.getSourceHost());
    }
    if (!StringUtils.isEmpty(result.getTargetHost())) {
      update.set(Fields.targetHost, result.getTargetHost());
    }
    if (!StringUtils.isEmpty(result.getCoreVersion())) {
      update.set(Fields.coreVersion, result.getCoreVersion());
    }
    if (!StringUtils.isEmpty(result.getExtVersion())) {
      update.set(Fields.extVersion, result.getExtVersion());
    }
    if (!StringUtils.isEmpty(result.getCaseRecordVersion())) {
      update.set(Fields.caseRecordVersion, result.getCaseRecordVersion());
    }
    if (result.getTotalCaseCount() != null) {
      update.set(Fields.totalCaseCount, result.getTotalCaseCount());
    }
    if (result.getReplayStartTime() != null) {
      update.set(Fields.replayStartTime, result.getReplayStartTime());
    }
    if (result.getReplayEndTime() != null) {
      update.set(Fields.replayEndTime, result.getReplayEndTime());
    }
    if (MapUtils.isNotEmpty(result.getCustomTags())) {
      update.set(Fields.customTags, result.getCustomTags());
    }
    if (MapUtils.isNotEmpty(result.getCaseTags())) {
      update.set(Fields.caseTags, result.getCaseTags());
    }

    ReportPlanStatisticCollection dao =
        mongoTemplate.findAndModify(
            Query.query(Criteria.where(Fields.planId).is(result.getPlanId())),
            update,
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            ReportPlanStatisticCollection.class);

    return true;
  }

  @Override
  public Pair<List<ReportPlanStatisticDto>, Long> pageQueryPlanStatistic(
      QueryPlanStatisticsRequestType request) {
    Query query = fillFilterConditions(request);
    Long totalCount = -1L;
    if (Boolean.TRUE.equals(request.getNeedTotal())) {
      totalCount = mongoTemplate.count(query, ReportPlanStatisticCollection.class);
    }

    Pageable pageable =
        PageRequest.of(request.getPageIndex() - 1, request.getPageSize(),
            Sort.by(Sort.Direction.DESC, Fields.planId));
    query.with(pageable);

    List<ReportPlanStatisticCollection> daos = mongoTemplate.find(query,
        ReportPlanStatisticCollection.class);
    List<ReportPlanStatisticDto> result =
        daos.stream().map(PlanMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    return new MutablePair<>(result, totalCount);
  }

  @Override
  public Long findReplayCount() {
    Query query = new Query();
    return mongoTemplate.count(query, ReportPlanStatisticCollection.class);
  }

  @Override
  public Long findReplayCountByAppId(String appId) {
    Query query = Query.query(Criteria.where(Fields.appId).is(appId));
    return mongoTemplate.count(query, ReportPlanStatisticCollection.class);
  }

  @Override
  public List<ReportPlanStatisticDto> findLatestSuccessPlanId(String rangeField, Long startTime,
      Long endTime,
      String matchField, Integer matchValue, String groupField, String orderField, boolean desc) {

    List<AggregationOperation> operations = new ArrayList<>();
    if (!StringUtils.isEmpty(rangeField)) {
      operations.add(Aggregation.match(Criteria.where(rangeField).gte(startTime).lte(endTime)));
    }
    if (!StringUtils.isEmpty(matchField)) {
      operations.add(Aggregation.match(Criteria.where(matchField).is(matchValue)));
    }

    Sort sort = null;
    if (desc) {
      sort = Sort.by(Sort.Direction.DESC, orderField);
    } else {
      sort = Sort.by(Sort.Direction.ASC, orderField);
    }
    SortOperation sortOperation = Aggregation.sort(sort);
    operations.add(sortOperation);

    GroupOperation groupOperation = Aggregation.group(Fields.appId, Fields.appName)
        .first(Fields.planId).as(Fields.planId)
        .first(ModelBase.Fields.dataChangeCreateTime).as(ModelBase.Fields.dataChangeCreateTime);
    operations.add(groupOperation);

    ProjectionOperation projectionOperation =
        Aggregation.project(Fields.planId, ModelBase.Fields.dataChangeCreateTime, Fields.appId,
            Fields.appName);
    operations.add(projectionOperation);
    AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(
        Aggregation.newAggregation(operations),
        ReportPlanStatisticCollection.class, BasicDBObject.class);
    return aggregate.getMappedResults().stream().map(this::covertToReportPlanStatisticDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<LatestDailySuccessPlanIdDto> findLatestDailySuccessPlanId(String rangeField,
      Long startTime,
      Long endTime, List<MutablePair<Object, Object>> matches, String groupField, String timeDate,
      String orderField,
      boolean desc) {

    List<AggregationOperation> operations = new ArrayList<>();

    if (!StringUtils.isEmpty(rangeField)) {
      operations.add(Aggregation.match(Criteria.where(rangeField).gte(startTime).lte(endTime)));
    }
    if (!CollectionUtils.isEmpty(matches)) {
      matches.forEach(item -> {
        operations.add(
            Aggregation.match(Criteria.where((String) item.getLeft()).is(item.getRight())));
      });
    }

    Sort sort = null;
    if (desc) {
      sort = Sort.by(Sort.Direction.DESC, orderField);
    } else {
      sort = Sort.by(Sort.Direction.ASC, orderField);
    }
    SortOperation sortOperation = Aggregation.sort(sort);
    operations.add(sortOperation);

    ProjectionOperation projectionOperation = Aggregation.project(Fields.planId,
            ModelBase.Fields.dataChangeCreateTime,
            groupField)
        .and(DateOperators.DateToString.dateOf(new AggregationExpression() {
          @Override
          public Document toDocument(AggregationOperationContext aggregationOperationContext) {
            Document document = new Document("$toDate", "$dataChangeCreateTime");
            return document;
          }
        }).toString("%Y-%m-%d").withTimezone(DateOperators.Timezone.valueOf("+08"))).as(DATE_TIME);
    operations.add(projectionOperation);

    GroupOperation groupOperation =
        Aggregation.group(DATE_TIME, groupField).first(groupField).as(groupField).first(DATE_TIME)
            .as(DATE_TIME)
            .first(Fields.planId).as(Fields.planId).first(ModelBase.Fields.dataChangeCreateTime)
            .as(ModelBase.Fields.dataChangeCreateTime);
    operations.add(groupOperation);

    AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(
        Aggregation.newAggregation(operations),
        ReportPlanStatisticCollection.class, BasicDBObject.class);
    return aggregate.getMappedResults().stream().map(this::covertToLatestDailySuccessPlanIdDto)
        .collect(Collectors.toList());
  }

  @Override
  public ReportPlanStatisticDto changePlanStatus(String planId, Integer status,
      Integer totalCaseCount, String errorMessage, Boolean rerun) {
    if (planId == null || planId == "") {
      return null;
    }
    Update update = MongoHelper.getUpdate();
    if (status != null) {
      update.set(Fields.status, status);
      if (status == ReplayStatusType.RERUNNING) {
        update.set(Fields.lastRerunStartTime, System.currentTimeMillis());
      }
    }
    if (totalCaseCount != null) {
      update.set(Fields.totalCaseCount, totalCaseCount);
    }
    if (errorMessage != null) {
      update.set(Fields.errorMessage, errorMessage);
    }
    if (rerun != null && !rerun) {
      update.set(Fields.replayEndTime, System.currentTimeMillis());
    }
    if (update.getUpdateObject().keySet().isEmpty()) {
      return null;
    }
    ReportPlanStatisticCollection plan =
        mongoTemplate.findAndModify(Query.query(Criteria.where(Fields.planId).is(planId)), update,
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            ReportPlanStatisticCollection.class);
    return PlanMapper.INSTANCE.dtoFromDao(plan);
  }

  @Override
  public boolean deletePlan(String planId) {
    Query query = Query.query(Criteria.where(Fields.planId).is(planId));
    DeleteResult deleteResult = mongoTemplate.remove(query, ReportPlanStatisticCollection.class);
    return deleteResult.getDeletedCount() > 0;
  }

  @Override
  public boolean removeErrorMsg(String planId) {
    Query query = Query.query(Criteria.where(Fields.planId).is(planId));
    Update update = MongoHelper.getUpdate();
    update.set(Fields.errorMessage, null);
    return mongoTemplate.updateMulti(query, update, ReportPlanStatisticCollection.class)
        .getMatchedCount() > 0;
  }

  private Query fillFilterConditions(QueryPlanStatisticsRequestType request) {
    Query query = new Query();
    if (request == null) {
      return query;
    }
    if (request.getPlanId() != null) {
      query.addCriteria(Criteria.where(Fields.planId).is(request.getPlanId()));
    }

    if (!StringUtils.isEmpty(request.getAppId())) {
      query.addCriteria(Criteria.where(Fields.appId).is(request.getAppId()));
    }

    if (!StringUtils.isEmpty(request.getImageId())) {
      query.addCriteria(Criteria.where(Fields.targetImageId).is(request.getImageId()));
    }

    return query;
  }

  private ReportPlanStatisticDto covertToReportPlanStatisticDto(BasicDBObject basicDBObject) {

    ReportPlanStatisticDto reportPlanStatisticDto = new ReportPlanStatisticDto();

    if (basicDBObject == null) {
      return reportPlanStatisticDto;
    }
    reportPlanStatisticDto.setAppId(basicDBObject.getString(Fields.appId));
    reportPlanStatisticDto.setAppName(basicDBObject.getString(Fields.appName));
    reportPlanStatisticDto.setPlanId(basicDBObject.getString(Fields.planId));
    reportPlanStatisticDto.setDataChangeCreateTime(
        basicDBObject.getLong(ModelBase.Fields.dataChangeCreateTime));
    return reportPlanStatisticDto;
  }

  private LatestDailySuccessPlanIdDto covertToLatestDailySuccessPlanIdDto(
      BasicDBObject basicDBObject) {
    LatestDailySuccessPlanIdDto latestDailySuccessPlanIdDto = new LatestDailySuccessPlanIdDto();
    if (basicDBObject == null) {
      return latestDailySuccessPlanIdDto;
    }
    latestDailySuccessPlanIdDto.setDateTime(basicDBObject.getString(DATE_TIME));
    latestDailySuccessPlanIdDto.setAppId(basicDBObject.getString(Fields.appId));
    latestDailySuccessPlanIdDto.setPlanId(basicDBObject.getString(Fields.planId));
    latestDailySuccessPlanIdDto.setDataChangeCreateTime(
        basicDBObject.getLong(ModelBase.Fields.dataChangeCreateTime));
    return latestDailySuccessPlanIdDto;
  }

}
