package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.dao.mongodb.entity.SceneDetail;
import com.arextest.web.model.dto.SceneDetailDto;

@Mapper
public interface SceneDetailMapper {
    SceneDetailMapper INSTANCE = Mappers.getMapper(SceneDetailMapper.class);

    SceneDetail daoFromDto(SceneDetailDto dto);

    SceneDetailDto dtoFromDao(SceneDetail dao);

}
