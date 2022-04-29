package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.common.Difference;
import com.arextest.report.model.dto.DifferenceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface DifferenceMapper {
    DifferenceMapper INSTANCE = Mappers.getMapper(DifferenceMapper.class);

    Difference contractFromDto(DifferenceDto dto);
}
