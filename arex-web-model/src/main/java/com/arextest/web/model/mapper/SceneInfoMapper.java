package com.arextest.web.model.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.QuerySceneInfoResponseType;
import com.arextest.web.model.dao.mongodb.iosummary.SceneInfoCollection;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.dto.iosummary.SubSceneInfo;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Mapper
public interface SceneInfoMapper {
    SceneInfoMapper INSTANCE = Mappers.getMapper(SceneInfoMapper.class);

    SceneInfoCollection daoFromDto(SceneInfo dto);

    SceneInfo dtoFromDao(SceneInfoCollection dao);

    QuerySceneInfoResponseType.SceneInfoType contractFromDto(SceneInfo dto);

    default List<QuerySceneInfoResponseType.SubSceneInfoType> mapSubScenes(SceneInfo dto) {
        List<SubSceneInfo> subSceneInfos = dto.getSubScenes();

        // the new scene information is stored in subSceneInfoMap
        Map<String, SubSceneInfo> infoMap = dto.getSubSceneInfoMap();
        if (infoMap != null) {
            subSceneInfos = new ArrayList<>(infoMap.values());
        }

        return Optional.ofNullable(subSceneInfos).orElse(Collections.emptyList()).stream()
            .map(SubSceneInfoMapper.INSTANCE::contractFromDto).collect(Collectors.toList());

    }

    @AfterMapping
    default void mapSubScenesAfterMapping(SceneInfo dto,
        @MappingTarget QuerySceneInfoResponseType.SceneInfoType sceneInfoType) {
        List<QuerySceneInfoResponseType.SubSceneInfoType> subSceneInfoTypes = mapSubScenes(dto);
        sceneInfoType.setSubScenes(subSceneInfoTypes);
    }
}
