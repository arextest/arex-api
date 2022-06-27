package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.filesystem.AddressType;
import com.arextest.report.model.dto.filesystem.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressType contractFromDto(AddressDto dto);
}
