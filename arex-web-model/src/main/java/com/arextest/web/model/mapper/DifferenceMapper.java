package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.common.Difference;
import com.arextest.web.model.dto.DifferenceDto;

@Mapper
public interface DifferenceMapper {
    DifferenceMapper INSTANCE = Mappers.getMapper(DifferenceMapper.class);

    Difference contractFromDto(DifferenceDto dto);
}
