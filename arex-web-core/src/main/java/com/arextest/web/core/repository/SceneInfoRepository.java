package com.arextest.web.core.repository;

import com.arextest.web.model.dto.iosummary.SceneInfo;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/28.
 */
public interface SceneInfoRepository extends RepositoryProvider {
    boolean save(List<SceneInfo> sceneInfos);

    SceneInfo save(SceneInfo sceneInfo);

    List<SceneInfo> querySceneInfo(String planId, String planItemId);

    boolean removeByPlanItemId(List<String> planItemIds);
}
