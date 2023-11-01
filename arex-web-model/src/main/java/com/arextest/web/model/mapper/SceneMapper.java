package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.common.Scene;
import com.arextest.web.model.dto.SceneDto;

@Mapper
public interface SceneMapper {
    SceneMapper INSTANCE = Mappers.getMapper(SceneMapper.class);

    Scene contractFromDto(SceneDto dto);
}
