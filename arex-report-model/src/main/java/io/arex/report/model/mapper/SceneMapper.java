package io.arex.report.model.mapper;

import io.arex.report.model.api.contracts.common.Scene;
import io.arex.report.model.dto.SceneDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SceneMapper {
    SceneMapper INSTANCE = Mappers.getMapper(SceneMapper.class);

    Scene contractFromDto(SceneDto dto);
}
