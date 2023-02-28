package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.iosummary.SceneInfoCollection;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Created by rchen9 on 2023/2/28.
 */
@Mapper
public interface SceneInfoMapper {
    SceneInfoMapper INSTANCE = Mappers.getMapper(SceneInfoMapper.class);

    SceneInfoCollection daoFromDto(SceneInfo dto);

    SceneInfo dtoFromDao(SceneInfoCollection dao);
}
