package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.filesystem.AddressType;
import com.arextest.web.model.dto.filesystem.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressType contractFromDto(AddressDto dto);
}
