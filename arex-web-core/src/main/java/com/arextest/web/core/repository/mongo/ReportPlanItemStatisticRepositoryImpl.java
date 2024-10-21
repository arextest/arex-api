package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ReportPlanItemStatisticRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.ReportPlanItemStatisticCollection;
import com.arextest.web.model.dto.PlanItemDto;
import com.arextest.web.model.enums.ReplayStatusType;
import com.arextest.web.model.mapper.PlanItemMapper;
import com.mongodb.client.result.DeleteResult;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ReportPlanItemStatisticRepositoryImpl implements ReportPlanItemStatisticRepository {

  private static final String DOT = ".";
  private static final String PLAN_ITEM_ID = "planItemId";
  private static final String CASES = "cases";
  private static final String FAIL_CASES = "failCases";
  private static final String ERROR_CASES = "errorCases";
  private static final String PLAN_ID = "planId";
  private static final String OPERATION_ID = "operationId";
  private static final String OPERATION_NAME = "operationName";
  private static final String SERVICE_NAME = "serviceName";
  private static final String STATUS = "status";
  private static final String REPLAY_START_TIME = "replayStartTime";
  private static final String REPLAY_END_TIME = "replayEndTime";
  private static final String TOTAL_CASE_COUNT = "totalCaseCount";
  private static final String ERROR_MESSAGE = "errorMessage";

  @Resource
  private MongoTemplate mongoTemplate;

  @Override
  public PlanItemDto updatePlanItems(PlanItemDto planItem) {
    Update update = MongoHelper.getUpdate();
    update.setOnInsert(PLAN_ID, planItem.getPlanId())
        .setOnInsert(OPERATION_ID, planItem.getOperationId())
        .setOnInsert(OPERATION_NAME, planItem.getOperationName())
        .setOnInsert(SERVICE_NAME, planItem.getServiceName())
        .setOnInsert(DATA_CHANGE_CREATE_TIME, System.currentTimeMillis())
        .setOnInsert(STATUS, ReplayStatusType.INIT);
    if (planItem.getCases() != null) {
      for (Map.Entry<String, Integer> c : planItem.getCases().entrySet()) {
        update.inc(CASES + DOT + c.getKey(), c.getValue());
      }
    }
    if (planItem.getFailCases() != null) {
      for (Map.Entry<String, Integer> f : planItem.getFailCases().entrySet()) {
        update.inc(FAIL_CASES + DOT + f.getKey(), f.getValue());
      }
    }
    if (planItem.getErrorCases() != null) {
      for (Map.Entry<String, Integer> e : planItem.getErrorCases().entrySet()) {
        update.inc(ERROR_CASES + DOT + e.getKey(), e.getValue());
      }
    }

    ReportPlanItemStatisticCollection dao =
        mongoTemplate.findAndModify(
            Query.query(Criteria.where(PLAN_ITEM_ID).is(planItem.getPlanItemId())), update,
            FindAndModifyOptions.options().upsert(true).returnNew(true),
            ReportPlanItemStatisticCollection.class);
    return PlanItemMapper.INSTANCE.dtoFromDao(dao);
  }

  @Override
  public boolean findAndModifyBaseInfo(PlanItemDto result) {
    if (result.getPlanItemId() == null) {
      return false;
    }
    Update update = MongoHelper.getUpdate();
    update.setOnInsert(DATA_CHANGE_CREATE_TIME, System.currentTimeMillis());
    if (result.getPlanId() != null) {
      update.set(PLAN_ID, result.getPlanId());
    }
    if (result.getOperationId() != null) {
      update.set(OPERATION_ID, result.getOperationId());
    }
    if (Strings.isNotBlank(result.getOperationName())) {
      update.set(OPERATION_NAME, result.getOperationName());
    }
    if (Strings.isNotBlank(result.getServiceName())) {
      update.set(SERVICE_NAME, result.getServiceName());
    }
    if (result.getStatus() != null) {
      update.set(STATUS, result.getStatus());
    }
    if (result.getReplayStartTime() != null) {
      update.set(REPLAY_START_TIME, result.getReplayStartTime());
    }
    if (result.getReplayEndTime() != null) {
      update.set(REPLAY_END_TIME, result.getReplayEndTime());
    }
    if (result.getTotalCaseCount() != null) {
      update.set(TOTAL_CASE_COUNT, result.getTotalCaseCount());
    }
    mongoTemplate.findAndModify(
        Query.query(Criteria.where(PLAN_ITEM_ID).is(result.getPlanItemId())), update,
        FindAndModifyOptions.options().upsert(true), ReportPlanItemStatisticCollection.class);
    return true;
  }

  @Override
  public boolean findAndModifyCaseMap(PlanItemDto result) {
    if (result.getPlanItemId() == null) {
      return false;
    }
    Update update = MongoHelper.getUpdate();
    if (result.getCases() != null) {
      update.set(CASES, result.getCases());
    }
    if (result.getFailCases() != null) {
      update.set(FAIL_CASES, result.getFailCases());
    }
    if (result.getErrorCases() != null) {
      update.set(ERROR_CASES, result.getErrorCases());
    }
    mongoTemplate.findAndModify(
        Query.query(Criteria.where(PLAN_ITEM_ID).is(result.getPlanItemId())), update,
        FindAndModifyOptions.options().upsert(true), ReportPlanItemStatisticCollection.class);
    return true;
  }

  @Override
  public List<PlanItemDto> findByPlanIdAndPlanItemId(String planId, String planItemId) {
    if (planId == null && planItemId == null) {
      return new ArrayList<>();
    }
    Query query = new Query();
    if (planId != null) {
      query.addCriteria(Criteria.where(PLAN_ID).is(planId));
    }
    if (planItemId != null) {
      query.addCriteria(Criteria.where(PLAN_ITEM_ID).is(planItemId));
    }
    List<ReportPlanItemStatisticCollection> result =
        mongoTemplate.find(query, ReportPlanItemStatisticCollection.class);
    return result.stream().map(PlanItemMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
  }

  @Override
  public PlanItemDto findByPlanItemId(String planItemId) {
    if (planItemId == null) {
      return null;
    }
    ReportPlanItemStatisticCollection result = mongoTemplate
        .findOne(Query.query(Criteria.where(PLAN_ITEM_ID).is(planItemId)),
            ReportPlanItemStatisticCollection.class);
    return PlanItemMapper.INSTANCE.dtoFromDao(result);
  }

  @Override
  public List<PlanItemDto> findByPlanIds(List<String> planIds) {
    if (CollectionUtils.isEmpty(planIds)) {
      return new ArrayList<>();
    }
    List<ReportPlanItemStatisticCollection> result = mongoTemplate
        .find(Query.query(Criteria.where(PLAN_ID).in(planIds)),
            ReportPlanItemStatisticCollection.class);
    return result.stream().map(PlanItemMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());

  }

  @Override
  public List<Integer> findStatusesByPlanId(String planId) {
    if (planId == null) {
      return new ArrayList<>();
    }
    List<ReportPlanItemStatisticCollection> planItemDaos = mongoTemplate
        .find(Query.query(Criteria.where(PLAN_ID).is(planId)),
            ReportPlanItemStatisticCollection.class);
    return planItemDaos.stream().map(ReportPlanItemStatisticCollection::getStatus)
        .collect(Collectors.toList());
  }

  @Override
  public PlanItemDto changePlanItemStatus(String planItemId, Integer status, Integer totalCaseCount,
      String errorMessage, boolean rerun) {
    if (planItemId == null || planItemId == "") {
      return null;
    }
    Update update = MongoHelper.getUpdate();
    if (status != null) {
      update.set(STATUS, status);
      // rerun does not update start time and end time
      if (!rerun) {
        if (Objects.equals(status, ReplayStatusType.RUNNING)) {
          update.set(REPLAY_START_TIME, System.currentTimeMillis());
        } else {
          update.set(REPLAY_END_TIME, System.currentTimeMillis());
        }
      }
    }
    if (totalCaseCount != null) {
      update.set(TOTAL_CASE_COUNT, totalCaseCount);
    }
    if (Strings.isNotEmpty(errorMessage)) {
      update.set(ERROR_MESSAGE, errorMessage);
    }

    if (update.getUpdateObject().keySet().isEmpty()) {
      return null;
    }
    ReportPlanItemStatisticCollection planItem =
        mongoTemplate.findAndModify(Query.query(Criteria.where(PLAN_ITEM_ID).is(planItemId)),
            update,
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            ReportPlanItemStatisticCollection.class);
    return PlanItemMapper.INSTANCE.dtoFromDao(planItem);
  }

  @Override
  public boolean deletePlanItemsByPlanId(String planId) {
    Query query = Query.query(Criteria.where(PLAN_ID).is(planId));
    DeleteResult deleteResult = mongoTemplate.remove(query,
        ReportPlanItemStatisticCollection.class);
    return deleteResult.getDeletedCount() > 0;
  }

  @Override
  public boolean removeErrorMsg(List<String> planItemIds) {
    Update update = MongoHelper.getUpdate();
    update.set(ERROR_MESSAGE, null);
    Query query = Query.query(Criteria.where(PLAN_ITEM_ID).in(planItemIds));
    return mongoTemplate.updateMulti(query, update, ReportPlanItemStatisticCollection.class).getMatchedCount() == planItemIds.size();
  }

  @Override
  public boolean deletePlanItemsByPlanItemIds(List<String> planItemIds) {
    Query query = Query.query(Criteria.where(PLAN_ITEM_ID).in(planItemIds));
    DeleteResult deleteResult = mongoTemplate.remove(query,
        ReportPlanItemStatisticCollection.class);
    return deleteResult.getDeletedCount() > 0;
  }
}
