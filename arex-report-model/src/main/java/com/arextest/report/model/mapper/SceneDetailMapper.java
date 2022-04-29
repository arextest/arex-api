package com.arextest.report.model.mapper;

import com.arextest.report.model.dao.mongodb.entity.SceneDetail;
import com.arextest.report.model.dto.SceneDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SceneDetailMapper {
    SceneDetailMapper INSTANCE = Mappers.getMapper(SceneDetailMapper.class);

    SceneDetail daoFromDto(SceneDetailDto dto);

    SceneDetailDto dtoFromDao(SceneDetail dao);

}
