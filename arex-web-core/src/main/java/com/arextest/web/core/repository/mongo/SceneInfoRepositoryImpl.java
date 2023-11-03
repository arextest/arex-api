package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.core.repository.mongo.util.MongoHelper;
import com.arextest.web.model.dao.mongodb.iosummary.SceneInfoCollection;
import com.arextest.web.model.dao.mongodb.iosummary.SubSceneInfoDao;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.dto.iosummary.SubSceneInfo;
import com.arextest.web.model.mapper.SceneInfoMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Component
public class SceneInfoRepositoryImpl implements SceneInfoRepository {

  private static final String DOT = ".";

  @Autowired
  MongoTemplate mongoTemplate;

  @Override
  public boolean save(List<SceneInfo> sceneInfos) {
    List<SceneInfoCollection> sceneInfoCollections =
        sceneInfos.stream().map(SceneInfoMapper.INSTANCE::daoFromDto).collect(Collectors.toList());
    Collection<SceneInfoCollection> insertAll = mongoTemplate.insertAll(sceneInfoCollections);
    return CollectionUtils.isNotEmpty(insertAll);
  }

  @Override
  public SceneInfo save(SceneInfo sceneInfo) {

    Update update = MongoHelper.getUpdate();
    update.inc(SceneInfoCollection.Fields.count);
    update.setOnInsert(SceneInfoCollection.Fields.dataCreateTime, new Date());
    update.setOnInsert(SceneInfoCollection.Fields.code, sceneInfo.getCode());

    Map<String, SubSceneInfo> subSceneInfoMap = sceneInfo.getSubSceneInfoMap();
    Query query = Query.query(
        Criteria.where(SceneInfoCollection.Fields.planId).is(sceneInfo.getPlanId())
            .and(SceneInfoCollection.Fields.planItemId).is(sceneInfo.getPlanItemId())
            .and(SceneInfoCollection.Fields.categoryKey).is(sceneInfo.getCategoryKey()));
    if (MapUtils.isNotEmpty(subSceneInfoMap)) {
      List<String> groupKeyList = new ArrayList<>(subSceneInfoMap.keySet());
      String groupKey = groupKeyList.get(0);
      String groupKeyName = toColumnName(SceneInfoCollection.Fields.subSceneInfoMap, groupKey);
      SubSceneInfo subSceneInfo = subSceneInfoMap.get(groupKey);

      update.inc(toColumnName(groupKeyName, SubSceneInfoDao.Fields.count));
      update.set(toColumnName(groupKeyName, SubSceneInfoDao.Fields.code), subSceneInfo.getCode());
      update.set(toColumnName(groupKeyName, SubSceneInfoDao.Fields.recordId),
          subSceneInfo.getRecordId());
      update.set(toColumnName(groupKeyName, SubSceneInfoDao.Fields.replayId),
          subSceneInfo.getReplayId());
      update.set(toColumnName(groupKeyName, SubSceneInfoDao.Fields.details),
          subSceneInfo.getDetails());
    }

    SceneInfoCollection andModify = mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().upsert(true).returnNew(true), SceneInfoCollection.class);
    return SceneInfoMapper.INSTANCE.dtoFromDao(andModify);
  }

  @Override
  public List<SceneInfo> querySceneInfo(String planId, String planItemId) {
    Query query = Query.query(Criteria.where(SceneInfoCollection.Fields.planId).is(planId)
        .and(SceneInfoCollection.Fields.planItemId).is(planItemId));
    Criteria reCalculatedQuery = Criteria.where(SceneInfoCollection.Fields.reCalculated).is(true);
    Criteria codeQuery = Criteria.where(SceneInfoCollection.Fields.code).nin(-1, 0);
    query.addCriteria(new Criteria().orOperator(reCalculatedQuery, codeQuery));

    List<SceneInfoCollection> sceneInfoCollections = mongoTemplate.find(query,
        SceneInfoCollection.class);
    return sceneInfoCollections.stream().map(SceneInfoMapper.INSTANCE::dtoFromDao)
        .collect(Collectors.toList());
  }

  @Override
  public boolean removeByPlanItemId(Set<String> planItemIds) {
    Query query = Query.query(Criteria.where(SceneInfoCollection.Fields.planItemId).in(planItemIds)
        .and(SceneInfoCollection.Fields.code).ne(0));
    return mongoTemplate.remove(query, SceneInfoCollection.class).getDeletedCount() > 0;
  }

  @Override
  public boolean removeById(Set<String> ids) {
    Query query = Query.query(Criteria.where(DASH_ID).in(ids));
    return mongoTemplate.remove(query, SceneInfoCollection.class).getDeletedCount() > 0;
  }

  @Override
  public boolean update(SceneInfo sceneInfo) {
    Update update = new Update();
    sceneInfo.setDataChangeCreateTime(null);
    sceneInfo.setDataChangeUpdateTime(System.currentTimeMillis());
    MongoHelper.appendFullProperties(update, sceneInfo);

    Query query = Query.query(Criteria.where(DASH_ID).is(sceneInfo.getId()));

    return mongoTemplate.findAndModify(query, update,
        FindAndModifyOptions.options().upsert(true).returnNew(true),
        SceneInfoCollection.class) != null;
  }

  private String toColumnName(String groupKeyName, String columnName) {
    return groupKeyName + DOT + columnName;
  }
}
