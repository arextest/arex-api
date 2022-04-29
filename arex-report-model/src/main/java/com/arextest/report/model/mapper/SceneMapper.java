package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.common.Scene;
import com.arextest.report.model.dto.SceneDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SceneMapper {
    SceneMapper INSTANCE = Mappers.getMapper(SceneMapper.class);

    Scene contractFromDto(SceneDto dto);
}
