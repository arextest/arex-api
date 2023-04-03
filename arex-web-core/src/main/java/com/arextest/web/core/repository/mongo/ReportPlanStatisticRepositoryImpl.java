package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsRequestType;
import com.arextest.web.model.dao.mongodb.ReportPlanStatisticCollection;
import com.arextest.web.model.dto.LatestDailySuccessPlanIdDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.mapper.PlanMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.result.DeleteResult;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ReportPlanStatisticRepositoryImpl implements ReportPlanStatisticRepository {
    private static final String PLAN_ID = "planId";
    private static final String PLAN_NAME = "planName";
    private static final String STATUS = "status";
    private static final String APP_ID = "appId";
    private static final String APP_NAME = "appName";
    private static final String CREATOR = "creator";
    private static final String TARGET_IMAGE_ID = "targetImageId";
    private static final String TARGET_IMAGE_NAME = "targetImageName";
    private static final String CASE_SOURCE_TYPE = "caseSourceType";
    private static final String SOURCE_ENV = "sourceEnv";
    private static final String TARGET_ENV = "targetEnv";
    private static final String SOURCE_HOST = "sourceHost";
    private static final String TARGET_HOST = "targetHost";
    private static final String CORE_VERSION = "coreVersion";
    private static final String EXT_VERSION = "extVersion";
    private static final String CASE_RECORD_VERSION = "caseRecordVersion";
    private static final String REPLAY_START_TIME = "replayStartTime";
    private static final String REPLAY_END_TIME = "replayEndTime";
    private static final String CASE_START_TIME = "caseStartTime";
    private static final String CASE_END_TIME = "caseEndTime";
    private static final String DATA_CHANGE_CREATE_TIME = "dataChangeCreateTime";
    private static final String TOTAL_CASE_COUNT = "totalCaseCount";
    private static final String DATE_TIME = "dateTime";
    private static final String CUSTOM_TAGS = "customTags";

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<ReportPlanStatisticDto> findByDataCreateTimeBetween(Date startTime, Date endTime) {
        Query query = new Query();
        Criteria criteria = Criteria.where(DATA_CHANGE_CREATE_TIME).gte(startTime).lte(endTime);
        query.addCriteria(criteria);
        List<ReportPlanStatisticCollection> result = mongoTemplate.find(query, ReportPlanStatisticCollection.class);
        return result.stream().map(PlanMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public ReportPlanStatisticDto findByPlanId(String planId) {
        if (planId == null) {
            return null;
        }
        ReportPlanStatisticCollection result = mongoTemplate.findOne(Query.query(Criteria.where(PLAN_ID).is(planId)),
                ReportPlanStatisticCollection.class);
        return PlanMapper.INSTANCE.dtoFromDao(result);
    }

    @Override
    public boolean findAndModifyBaseInfo(ReportPlanStatisticDto result) {
        if (result.getPlanId() == null) {
            return false;
        }

        Update update = MongoHelper.getUpdate();
        update.setOnInsert(DATA_CHANGE_CREATE_TIME, System.currentTimeMillis());

        if (result.getCaseStartTime() != null) {
            update.set(CASE_START_TIME, result.getCaseStartTime());
        }
        if (result.getCaseEndTime() != null) {
            update.set(CASE_END_TIME, result.getCaseEndTime());
        }
        if (!StringUtils.isEmpty(result.getPlanName())) {
            update.set(PLAN_NAME, result.getPlanName());
        }
        if (result.getStatus() != null) {
            update.set(STATUS, result.getStatus());
        }
        if (!StringUtils.isEmpty(result.getAppId())) {
            update.set(APP_ID, result.getAppId());
        }
        if (!StringUtils.isEmpty(result.getAppName())) {
            update.set(APP_NAME, result.getAppName());
        }
        if (!StringUtils.isEmpty(result.getCreator())) {
            update.set(CREATOR, result.getCreator());
        }
        if (result.getTargetImageId() != null) {
            update.set(TARGET_IMAGE_ID, result.getTargetImageId());
        }
        if (!StringUtils.isEmpty(result.getTargetImageName())) {
            update.set(TARGET_IMAGE_NAME, result.getTargetImageName());
        }
        if (result.getCaseSourceType() != null) {
            update.set(CASE_SOURCE_TYPE, result.getCaseSourceType());
        }
        if (!StringUtils.isEmpty(result.getSourceEnv())) {
            update.set(SOURCE_ENV, result.getSourceEnv());
        }
        if (!StringUtils.isEmpty(result.getTargetEnv())) {
            update.set(TARGET_ENV, result.getTargetEnv());
        }
        if (!StringUtils.isEmpty(result.getSourceHost())) {
            update.set(SOURCE_HOST, result.getSourceHost());
        }
        if (!StringUtils.isEmpty(result.getTargetHost())) {
            update.set(TARGET_HOST, result.getTargetHost());
        }
        if (!StringUtils.isEmpty(result.getCoreVersion())) {
            update.set(CORE_VERSION, result.getCoreVersion());
        }
        if (!StringUtils.isEmpty(result.getExtVersion())) {
            update.set(EXT_VERSION, result.getExtVersion());
        }
        if (!StringUtils.isEmpty(result.getCaseRecordVersion())) {
            update.set(CASE_RECORD_VERSION, result.getCaseRecordVersion());
        }
        if (result.getTotalCaseCount() != null) {
            update.set(TOTAL_CASE_COUNT, result.getTotalCaseCount());
        }
        if (result.getReplayStartTime() != null) {
            update.set(REPLAY_START_TIME, result.getReplayStartTime());
        }
        if (result.getReplayEndTime() != null) {
            update.set(REPLAY_END_TIME, result.getReplayEndTime());
        }
        if (MapUtils.isNotEmpty(result.getCustomTags())) {
            update.set(CUSTOM_TAGS, result.getCustomTags());
        }

        ReportPlanStatisticCollection dao = mongoTemplate.findAndModify(
                Query.query(Criteria.where(PLAN_ID).is(result.getPlanId())),
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                ReportPlanStatisticCollection.class);

        return true;
    }

    @Override
    public Pair<List<ReportPlanStatisticDto>, Long> pageQueryPlanStatistic(QueryPlanStatisticsRequestType request) {
        Query query = fillFilterConditions(request);
        Long totalCount = -1L;
        if (Boolean.TRUE.equals(request.getNeedTotal())) {
            totalCount = mongoTemplate.count(query, ReportPlanStatisticCollection.class);
        }

        Pageable pageable = PageRequest.of(request.getPageIndex() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, PLAN_ID));
        query.with(pageable);

        List<ReportPlanStatisticCollection> daos = mongoTemplate.find(query, ReportPlanStatisticCollection.class);
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
        Query query = Query.query(Criteria.where(APP_ID).is(appId));
        return mongoTemplate.count(query, ReportPlanStatisticCollection.class);
    }

    @Override
    public List<ReportPlanStatisticDto> findLatestSuccessPlanId(String rangeField, Long startTime, Long endTime,
            String matchField, Integer matchValue,
            String groupField, String orderField, boolean desc) {

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

        GroupOperation groupOperation = Aggregation.group(APP_ID, APP_NAME)
                .first(PLAN_ID)
                .as(PLAN_ID)
                .first(DATA_CHANGE_CREATE_TIME)
                .as(DATA_CHANGE_CREATE_TIME);
        operations.add(groupOperation);

        ProjectionOperation projectionOperation =
                Aggregation.project(PLAN_ID, DATA_CHANGE_CREATE_TIME, APP_ID, APP_NAME);
        operations.add(projectionOperation);
        AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(Aggregation.newAggregation(operations),
                ReportPlanStatisticCollection.class, BasicDBObject.class);
        return aggregate.getMappedResults()
                .stream()
                .map(this::covertToReportPlanStatisticDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LatestDailySuccessPlanIdDto> findLatestDailySuccessPlanId(String rangeField,
            Long startTime,
            Long endTime,
            List<MutablePair<Object, Object>> matches,
            String groupField,
            String timeDate,
            String orderField,
            boolean desc) {

        List<AggregationOperation> operations = new ArrayList<>();

        if (!StringUtils.isEmpty(rangeField)) {
            operations.add(Aggregation.match(Criteria.where(rangeField).gte(startTime).lte(endTime)));
        }
        if (!CollectionUtils.isEmpty(matches)) {
            matches.forEach(item -> {
                operations.add(Aggregation.match(Criteria.where((String) item.getLeft()).is(item.getRight())));
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

        ProjectionOperation projectionOperation = Aggregation.project(PLAN_ID, DATA_CHANGE_CREATE_TIME, groupField).and(
                DateOperators.DateToString.dateOf(new AggregationExpression() {
                    @Override
                    public Document toDocument(AggregationOperationContext aggregationOperationContext) {
                        Document document = new Document("$toDate", "$dataChangeCreateTime");
                        return document;
                    }
                }).toString("%Y-%m-%d").withTimezone(DateOperators.Timezone.valueOf("+08"))).as(DATE_TIME);
        operations.add(projectionOperation);


        GroupOperation groupOperation = Aggregation.group(DATE_TIME, groupField)
                .first(groupField).as(groupField)
                .first(DATE_TIME).as(DATE_TIME)
                .first(PLAN_ID).as(PLAN_ID)
                .first(DATA_CHANGE_CREATE_TIME).as(DATA_CHANGE_CREATE_TIME);
        operations.add(groupOperation);

        AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(Aggregation.newAggregation(operations),
                ReportPlanStatisticCollection.class, BasicDBObject.class);
        return aggregate.getMappedResults()
                .stream()
                .map(this::covertToLatestDailySuccessPlanIdDto)
                .collect(Collectors.toList());
    }



    @Override
    public ReportPlanStatisticDto changePlanStatus(String planId, Integer status, Integer totalCaseCount) {
        if (planId == null || planId == "") {
            return null;
        }
        Update update = MongoHelper.getUpdate();
        if (status != null) {
            update.set(STATUS, status);
        }
        if (totalCaseCount != null) {
            update.set(TOTAL_CASE_COUNT, totalCaseCount);
        }

        update.set(REPLAY_END_TIME, System.currentTimeMillis());
        if (update.getUpdateObject().keySet().size() == 0) {
            return null;
        }
        ReportPlanStatisticCollection plan =
                mongoTemplate.findAndModify(Query.query(Criteria.where(PLAN_ID).is(planId)),
                        update,
                        FindAndModifyOptions.options().returnNew(true).upsert(true),
                        ReportPlanStatisticCollection.class);
        return PlanMapper.INSTANCE.dtoFromDao(plan);
    }

    @Override
    public boolean deletePlan(String planId) {
        Query query = Query.query(Criteria.where(PLAN_ID).is(planId));
        DeleteResult deleteResult = mongoTemplate.remove(query, ReportPlanStatisticCollection.class);
        return deleteResult.getDeletedCount() > 0;
    }

    private Query fillFilterConditions(QueryPlanStatisticsRequestType request) {
        Query query = new Query();
        if (request == null) {
            return query;
        }
        if (request.getPlanId() != null) {
            query.addCriteria(Criteria.where(PLAN_ID).is(request.getPlanId()));
        }

        if (!StringUtils.isEmpty(request.getAppId())) {
            query.addCriteria(Criteria.where(APP_ID).is(request.getAppId()));
        }

        if (!StringUtils.isEmpty(request.getImageId())) {
            query.addCriteria(Criteria.where(TARGET_IMAGE_ID).is(request.getImageId()));
        }

        return query;
    }

    private ReportPlanStatisticDto covertToReportPlanStatisticDto(BasicDBObject basicDBObject) {

        ReportPlanStatisticDto reportPlanStatisticDto = new ReportPlanStatisticDto();

        if (basicDBObject == null) {
            return reportPlanStatisticDto;
        }
        reportPlanStatisticDto.setAppId(basicDBObject.getString(APP_ID));
        reportPlanStatisticDto.setAppName(basicDBObject.getString(APP_NAME));
        reportPlanStatisticDto.setPlanId(basicDBObject.getString(PLAN_ID));
        reportPlanStatisticDto.setDataChangeCreateTime(basicDBObject.getLong(DATA_CHANGE_CREATE_TIME));
        return reportPlanStatisticDto;
    }

    private LatestDailySuccessPlanIdDto covertToLatestDailySuccessPlanIdDto(BasicDBObject basicDBObject) {
        LatestDailySuccessPlanIdDto latestDailySuccessPlanIdDto = new LatestDailySuccessPlanIdDto();
        if (basicDBObject == null) {
            return latestDailySuccessPlanIdDto;
        }
        latestDailySuccessPlanIdDto.setDateTime(basicDBObject.getString(DATE_TIME));
        latestDailySuccessPlanIdDto.setAppId(basicDBObject.getString(APP_ID));
        latestDailySuccessPlanIdDto.setPlanId(basicDBObject.getString(PLAN_ID));
        latestDailySuccessPlanIdDto.setDataChangeCreateTime(basicDBObject.getLong(DATA_CHANGE_CREATE_TIME));
        return latestDailySuccessPlanIdDto;
    }

}
