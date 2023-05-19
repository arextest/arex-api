package com.arextest.web.core.repository.mongo;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.model.dao.mongodb.ReplayCompareResultCollection;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.mapper.CompareResultMapper;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean saveResults(List<CompareResultDto> results) {
        try {
            List<ReplayCompareResultCollection> rs = new ArrayList<>(results.size());
            for (CompareResultDto cr : results) {
                rs.add(CompareResultMapper.INSTANCE.daoFromDto(cr));
            }
            mongoTemplate.insertAll(rs);
            results.clear();
            for (ReplayCompareResultCollection r : rs) {
                results.add(CompareResultMapper.INSTANCE.dtoFromDao(r));
            }
            return true;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "failed to insert compare results", e);
        }
        return false;
    }

    @Override
    public List<CompareResultDto> findResultWithoutMsg(String planItemId) {
        if (planItemId == null) {
            return new ArrayList<>();
        }
        Query query = fillFilterConditions(null, planItemId, null, null, null);
        query.fields().exclude(BASE_MSG).exclude(TEST_MSG).exclude(LOGS);

        List<ReplayCompareResultCollection> results = mongoTemplate.find(query, ReplayCompareResultCollection.class);
        return results.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    @Override
    public List<CompareResultDto> findResultWithoutMsg(String planItemId, String keyWord) {
        Query query = fillFilterConditions(null, planItemId, null, null, keyWord);

        query.fields().include(PLAN_ITEM_ID);
        query.fields().include(PLAN_ID);
        query.fields().include(OPERATION_ID);
        query.fields().include(CATEGORY_NAME);
        query.fields().include(OPERATION_NAME);
        query.fields().include(REPLAY_ID);
        query.fields().include(RECORD_ID);
        query.fields().include(DIFF_RESULT_CODE);
        List<ReplayCompareResultCollection> result = mongoTemplate.find(query, ReplayCompareResultCollection.class);
        return result.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    }

    // @Override
    // public Pair<List<CompareResultDto>, Long> pageQueryWithoutMsg(Long planId, Long planItemId, String categoryName,
    //                                                               Integer resultType, String keyWord,
    //                                                               Integer pageIndex, Integer pageSize, Boolean needTotal) {
    //     Query query = fillFilterConditions(planId, planItemId, categoryName, resultType, keyWord);
    //
    //     query.fields().include(PLAN_ITEM_ID);
    //     query.fields().include(PLAN_ID);
    //     query.fields().include(OPERATION_ID);
    //     query.fields().include(CATEGORY_NAME);
    //     query.fields().include(OPERATION_NAME);
    //     query.fields().include(REPLAY_ID);
    //     query.fields().include(RECORD_ID);
    //     query.fields().include(DIFF_RESULT_CODE);
    //     Long totalCount = -1L;
    //     if (needTotal) {
    //         totalCount = mongoTemplate.count(query, ReplayCompareResultCollection.class);
    //     }
    //     Pageable pageable = new PageRequest(pageIndex, pageSize, Sort.by(direct));
    //     query.with(pageable);
    //     List<ReplayCompareResultCollection> result = mongoTemplate.find(query, ReplayCompareResultCollection.class);
    //     List<CompareResultDto> dtos =
    //             result.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
    //
    //     return new MutablePair<>(dtos, totalCount);
    // }


    @Override
    public Pair<List<CompareResultDto>, Long> queryCompareResultByPage(String planId,
                                                                       Integer pageSize,
                                                                       Integer pageIndex) {
        Query query = Query.query(Criteria.where(PLAN_ID).is(planId));
        query.fields().exclude(BASE_MSG).exclude(TEST_MSG);

        Long totalCount = mongoTemplate.count(query, ReplayCompareResultCollection.class);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        query.with(pageable);
        List<ReplayCompareResultCollection> daos = mongoTemplate.find(query, ReplayCompareResultCollection.class);
        List<CompareResultDto> dtos =
                daos.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());
        return new MutablePair<>(dtos, totalCount);
    }

    @Override
    public Pair<List<CompareResultDto>, Long> queryAllDiffMsgByPage(String planItemId,
                                                                    String recordId,
                                                                    List<Integer> diffResultCodeList,
                                                                    Integer pageSize,
                                                                    Integer pageIndex,
                                                                    Boolean needTotal) {
        Query query = Query.query(
                Criteria.where(PLAN_ITEM_ID).is(planItemId)
                        .and(RECORD_ID).is(recordId)
        );

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
        List<ReplayCompareResultCollection> result = mongoTemplate.find(query, ReplayCompareResultCollection.class);
        List<CompareResultDto> dtos =
                result.stream().map(CompareResultMapper.INSTANCE::dtoFromDao).collect(Collectors.toList());

        return new MutablePair<>(dtos, totalCount);
    }

    @Override
    public CompareResultDto queryCompareResultsById(String objectId) {
        Query query = new Query();
        query.addCriteria(Criteria.where(DASH_ID).is(objectId));
        ReplayCompareResultCollection result = mongoTemplate.findOne(query, ReplayCompareResultCollection.class);
        return CompareResultMapper.INSTANCE.dtoFromDao(result);
    }

    @Override
    public List<CompareResultDto> queryCompareResultsByRecordId(String planItemId, String recordId) {
        Query query = new Query();
        query.addCriteria(Criteria.where(RECORD_ID).is(recordId).and(PLAN_ITEM_ID).is(planItemId));
        List<ReplayCompareResultCollection> daos = mongoTemplate.find(query, ReplayCompareResultCollection.class);
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
        return mongoTemplate.findDistinct(query, RECORD_ID, ReplayCompareResultCollection.class, String.class).size();
    }

    @Override
    public List<CompareResultDto> queryCompareResults(String planId,
                                                      List<String> planItemIdList,
                                                      List<String> recordIdList,
                                                      List<Integer> diffResultCodeList) {
        Query query = Query.query(
                Criteria.where(PLAN_ID).is(planId)
        );
        if (CollectionUtils.isNotEmpty(planItemIdList)) {
            query.addCriteria(Criteria.where(PLAN_ITEM_ID).in(planItemIdList));
        }
        if (CollectionUtils.isNotEmpty(recordIdList)) {
            query.addCriteria(Criteria.where(RECORD_ID).in(recordIdList));
        }
        if (CollectionUtils.isNotEmpty(diffResultCodeList)) {
            query.addCriteria(Criteria.where(DIFF_RESULT_CODE).in(diffResultCodeList));
        }
        query.fields().include(OPERATION_ID);
        query.fields().include(RECORD_ID);
        List<ReplayCompareResultCollection> daos = mongoTemplate.find(query,
                ReplayCompareResultCollection.class);
        return daos.stream().
                map(CompareResultMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());

    }

    private Query fillFilterConditions(String planId, String planItemId, String categoryName, Integer resultType,
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
            query.addCriteria(new Criteria().orOperator(Criteria.where(REPLAY_ID).regex(".*?" + keyWord + ".*"),
                    Criteria.where(RECORD_ID).regex(".*?" + keyWord + ".*")));
        }
        return query;
    }
}
