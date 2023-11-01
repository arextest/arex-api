package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.common.Difference;
import com.arextest.web.model.dto.DifferenceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DifferenceMapper {

  DifferenceMapper INSTANCE = Mappers.getMapper(DifferenceMapper.class);

  Difference contractFromDto(DifferenceDto dto);
}
