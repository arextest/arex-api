package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.iosummary.SceneInfoCollection;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.dto.iosummary.SubSceneInfo;
import com.arextest.web.model.mapper.SceneInfoMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Component
public class SceneInfoRepositoryImpl implements SceneInfoRepository {

    private static final String PLAN_ID = "planId";

    private static final String PLAN_ITEM_ID = "planItemId";

    private static final String CODE = "code";

    private static final String COUNT = "count";

    private static final String CATEGORY_KEY = "categoryKey";

    private static final String SUB_SCENE_INFO_MAP = "subSceneInfoMap";

    private static final String DATA_CREATE_TIME = "dataCreateTime";

    private static final String RECORD_ID = "recordId";

    private static final String REPLAY_ID = "replayId";

    private static final String DETAILS = "details";

    private static final String DOT = ".";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean save(List<SceneInfo> sceneInfos) {
        List<SceneInfoCollection> sceneInfoCollections =
                sceneInfos.stream()
                        .map(SceneInfoMapper.INSTANCE::daoFromDto)
                        .collect(Collectors.toList());
        Collection<SceneInfoCollection> insertAll =
                mongoTemplate.insertAll(sceneInfoCollections);
        return CollectionUtils.isNotEmpty(insertAll);
    }

    @Override
    public SceneInfo save(SceneInfo sceneInfo) {

        Update update = MongoHelper.getUpdate();
        update.inc(COUNT);
        update.setOnInsert(DATA_CREATE_TIME, new Date());
        update.setOnInsert(CODE, sceneInfo.getCode());

        Map<String, SubSceneInfo> subSceneInfoMap = sceneInfo.getSubSceneInfoMap();
        Query query = Query.query(Criteria.where(PLAN_ID).is(sceneInfo.getPlanId())
                .and(PLAN_ITEM_ID).is(sceneInfo.getPlanItemId())
                .and(CATEGORY_KEY).is(sceneInfo.getCategoryKey())
        );
        if (MapUtils.isNotEmpty(subSceneInfoMap)) {
            List<String> groupKeyList = new ArrayList<>(subSceneInfoMap.keySet());
            String groupKey = groupKeyList.get(0);
            String groupKeyName = toColumnName(SUB_SCENE_INFO_MAP, groupKey);
            SubSceneInfo subSceneInfo = subSceneInfoMap.get(groupKey);

            update.inc(toColumnName(groupKeyName, COUNT));
            update.setOnInsert(toColumnName(groupKeyName, CODE), subSceneInfo.getCode());
            update.setOnInsert(toColumnName(groupKeyName, RECORD_ID), subSceneInfo.getRecordId());
            update.setOnInsert(toColumnName(groupKeyName, REPLAY_ID), subSceneInfo.getReplayId());
            update.setOnInsert(toColumnName(groupKeyName, DETAILS), subSceneInfo.getDetails());
        }

        SceneInfoCollection andModify = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                SceneInfoCollection.class
        );
        return SceneInfoMapper.INSTANCE.dtoFromDao(andModify);
    }

    @Override
    public List<SceneInfo> querySceneInfo(String planId, String planItemId) {
        Query query = Query.query(Criteria.where(PLAN_ID).is(planId)
                .and(PLAN_ITEM_ID).is(planItemId)
                .and(CODE).nin(-1, 0)
        );
        List<SceneInfoCollection> sceneInfoCollections =
                mongoTemplate.find(query, SceneInfoCollection.class);
        return sceneInfoCollections.stream()
                .map(SceneInfoMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }

    private String toColumnName(String groupKeyName, String columnName) {
        return groupKeyName + DOT + columnName;
    }
}
