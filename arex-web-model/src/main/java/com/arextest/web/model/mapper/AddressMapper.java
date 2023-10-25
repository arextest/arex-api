package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.filesystem.AddressType;
import com.arextest.web.model.dto.filesystem.AddressDto;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressType contractFromDto(AddressDto dto);
}
