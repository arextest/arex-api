package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Component
public class SceneInfoRepositoryImpl implements SceneInfoRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean save(List<SceneInfo> sceneInfos) {
        Collection<SceneInfo> insertAll = mongoTemplate.insertAll(sceneInfos);
        return CollectionUtils.isNotEmpty(insertAll);
    }
}
