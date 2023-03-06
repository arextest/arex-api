package com.arextest.web.core.repository.mongo;

import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.model.dao.mongodb.iosummary.SceneInfoCollection;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.mapper.SceneInfoMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Component
public class SceneInfoRepositoryImpl implements SceneInfoRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean save(List<SceneInfo> sceneInfos) {
        List<SceneInfoCollection> sceneInfoCollections =
                sceneInfos.stream()
                        .map(SceneInfoMapper.INSTANCE::daoFromDto)
                        .collect(Collectors.toList());
        Collection<SceneInfoCollection> insertAll = mongoTemplate.insertAll(sceneInfoCollections);
        return CollectionUtils.isNotEmpty(insertAll);
    }
}
