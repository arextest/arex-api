package com.arextest.report.core.repository.mongo;

import com.arextest.report.core.repository.ReportDiffAggStatisticRepository;
import com.arextest.report.core.repository.mongo.util.MongoHelper;
import com.arextest.report.model.dao.mongodb.ReportDiffAggStatisticCollection;
import com.arextest.report.model.dao.mongodb.entity.SceneDetail;
import com.arextest.report.model.dto.DiffAggDto;
import com.arextest.report.model.dto.DifferenceDto;
import com.arextest.report.model.dto.SceneDetailDto;
import com.arextest.report.model.dto.SceneDto;
import com.arextest.report.model.mapper.DiffAggMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class ReportDiffAggStatisticRepositoryImpl implements ReportDiffAggStatisticRepository {
    private static final String DOT = ".";
    private static final String PLAN_ITEM_ID = "planItemId";
    private static final String PLAN_ID = "planId";
    private static final String OPERATION_ID = "operationId";
    private static final String CATEGORY_NAME = "categoryName";
    private static final String OPERATION_NAME = "operationName";
    private static final String EMPTY_NODE = "%empty%";
    private static final String DIFFERENCES = "differences";
    private static final String COMPARE_RESULT_ID = "compareResultId";
    private static final String LOG_INDEXES = "logIndexes";
    private static final String DIFF_CASE_COUNT = "diffCaseCounts";
    private static final String SCENE_COUNT = "sceneCount";

    @Resource
    private MongoTemplate mongoTemplate;

    
    @Override
    public DiffAggDto updateDiffScenes(DiffAggDto dto) {
        if (dto == null) {
            return null;
        }
        Update update = MongoHelper.getUpdate();
        update.setOnInsert(PLAN_ID, dto.getPlanId())
                .setOnInsert(OPERATION_ID, dto.getOperationId())
                .setOnInsert(DATA_CHANGE_CREATE_TIME, System.currentTimeMillis());

        for (Map.Entry<String, Integer> diffCounts : dto.getDiffCaseCounts().entrySet()) {
            String difSceneKey = diffCounts.getKey();
            if (StringUtils.isEmpty(diffCounts.getKey())) {
                difSceneKey = EMPTY_NODE;
            }
            String key = DIFF_CASE_COUNT + DOT + difSceneKey;
            update.inc(key, diffCounts.getValue());
        }

        for (Map.Entry<String, Map<String, SceneDetailDto>> diffScene : dto.getDifferences().entrySet()) {
            if (diffScene.getValue() == null) {
                continue;
            }
            for (Map.Entry<String, SceneDetailDto> scene : diffScene.getValue().entrySet()) {
                String difSceneKey = diffScene.getKey();
                if (StringUtils.isEmpty(difSceneKey)) {
                    difSceneKey = EMPTY_NODE;
                }
                String sceneKey = scene.getKey();
                if (StringUtils.isEmpty(sceneKey)) {
                    sceneKey = EMPTY_NODE;
                }
                String key = DIFFERENCES + DOT + difSceneKey + DOT + sceneKey;

                SceneDetailDto detailDto = scene.getValue();
                update.set(key + DOT + COMPARE_RESULT_ID, detailDto.getCompareResultId());
                update.set(key + DOT + LOG_INDEXES, detailDto.getLogIndexes());
                update.inc(key + DOT + SCENE_COUNT, detailDto.getSceneCount());
            }
        }
        ReportDiffAggStatisticCollection dao = mongoTemplate.findAndModify(
                Query.query(Criteria.where(PLAN_ITEM_ID).is(dto.getPlanItemId())
                        .and(CATEGORY_NAME).is(dto.getCategoryName())
                        .and(OPERATION_NAME).is(dto.getOperationName())),
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                ReportDiffAggStatisticCollection.class
        );
        return DiffAggMapper.INSTANCE.dtoFromDao(dao);
    }

    
    @Override
    public List<DifferenceDto> queryDifferences(String planItemId, String categoryName, String operationName) {
        List<DifferenceDto> differenceDtos = new ArrayList<>();

        Query query = Query.query(Criteria.where(PLAN_ITEM_ID).is(planItemId)
                .and(CATEGORY_NAME).is(categoryName)
                .and(OPERATION_NAME).is(operationName));
        ReportDiffAggStatisticCollection dao = mongoTemplate.findOne(query, ReportDiffAggStatisticCollection.class);

        if (dao == null) {
            return differenceDtos;
        }
        Map<String, Integer> diffCaseCounts = dao.getDiffCaseCounts();
        if (diffCaseCounts == null) {
            return differenceDtos;
        }
        if (dao.getDifferences() == null) {
            return differenceDtos;
        }
        dao.getDifferences().forEach((k, v) -> {
            DifferenceDto dto = new DifferenceDto();
            dto.setDifferenceName(k);
            dto.setSceneCount(v.size());
            if (diffCaseCounts != null && diffCaseCounts.containsKey(k)) {
                dto.setCaseCount(diffCaseCounts.get(k));
            } else {
                
                LOGGER.error("case count should not be zero");
                dto.setCaseCount(0);
            }
            differenceDtos.add(dto);
        });
        differenceDtos.sort(Comparator.comparing(DifferenceDto::getCaseCount).reversed());
        return differenceDtos;
    }

    
    @Override
    public List<SceneDto> queryScenesByDifference(String planItemId,
            String categoryName,
            String operationName,
            String diffName) {

        List<SceneDto> scenes = new ArrayList<>();

        Query query = Query.query(Criteria.where(PLAN_ITEM_ID).is(planItemId)
                .and(CATEGORY_NAME).is(categoryName)
                .and(OPERATION_NAME).is(operationName));
        query.fields().include(DIFFERENCES + DOT + diffName)
                .include(PLAN_ITEM_ID)
                .include(CATEGORY_NAME)
                .include(OPERATION_NAME);
        ReportDiffAggStatisticCollection dao = mongoTemplate.findOne(query, ReportDiffAggStatisticCollection.class);
        if (dao == null || dao.getDifferences() == null || !dao.getDifferences().containsKey(diffName)) {
            return scenes;
        }
        Map<String, SceneDetail> sceneMap = dao.getDifferences().get(diffName);
        sceneMap.forEach((k, v) -> {
            SceneDto sceneDto = new SceneDto();
            sceneDto.setSceneName(k);
            sceneDto.setCompareResultId(v.getCompareResultId());
            sceneDto.setLogIndexes(v.getLogIndexes());
            scenes.add(sceneDto);
        });
        return scenes;
    }
}
