package com.arextest.web.model.mapper;

import com.arextest.web.model.dto.filesystem.FSNodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FSNodeMapper {

  FSNodeMapper INSTANCE = Mappers.getMapper(FSNodeMapper.class);

  FSNodeDto copy(FSNodeDto dto);
}
