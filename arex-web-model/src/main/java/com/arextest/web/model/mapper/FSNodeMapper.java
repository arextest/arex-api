package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.dto.filesystem.FSNodeDto;

@Mapper
public interface FSNodeMapper {
    FSNodeMapper INSTANCE = Mappers.getMapper(FSNodeMapper.class);

    FSNodeDto copy(FSNodeDto dto);
}
