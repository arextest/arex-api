package io.arex.report.model.mapper;

import io.arex.report.model.api.contracts.common.Difference;
import io.arex.report.model.dto.DifferenceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DifferenceMapper {
    DifferenceMapper INSTANCE = Mappers.getMapper(DifferenceMapper.class);

    Difference contractFromDto(DifferenceDto dto);
}
