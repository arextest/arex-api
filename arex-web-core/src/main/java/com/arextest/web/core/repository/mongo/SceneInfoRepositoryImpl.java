package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.model.dao.mongodb.iosummary.SceneInfoCollection;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.mapper.SceneInfoMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Component
public class SceneInfoRepositoryImpl implements SceneInfoRepository {

    private static final String PLAN_ID = "planId";

    private static final String PLAN_ITEM_ID = "planItemId";

    private static final String CODE = "code";
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
    public List<SceneInfo> querySceneInfo(String planId, String planItemId) {
        Query query = Query.query(Criteria.where(PLAN_ID).is(planId)
                .and(PLAN_ITEM_ID).is(planItemId)
                .and(CODE).ne(0));
        List<SceneInfoCollection> sceneInfoCollections =
                mongoTemplate.find(query, SceneInfoCollection.class);
        return sceneInfoCollections.stream()
                .map(SceneInfoMapper.INSTANCE::dtoFromDao)
                .collect(Collectors.toList());
    }
}
