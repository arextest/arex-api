package com.arextest.report.model.mapper;

import com.arextest.report.model.dto.filesystem.FSNodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FSNodeMapper {
    FSNodeMapper INSTANCE = Mappers.getMapper(FSNodeMapper.class);

    FSNodeDto copy(FSNodeDto dto);
}
