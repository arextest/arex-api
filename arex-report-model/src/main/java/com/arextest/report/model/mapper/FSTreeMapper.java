package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.filesystem.FSTreeType;
import com.arextest.report.model.dao.mongodb.FSTreeCollection;
import com.arextest.report.model.dto.FSTreeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FSTreeMapper {
    FSTreeMapper INSTANCE = Mappers.getMapper(FSTreeMapper.class);

    FSTreeDto dtoFromDao(FSTreeCollection dao);

    FSTreeCollection daoFromDto(FSTreeDto dto);

    FSTreeType contractFromDto(FSTreeDto dto);
}
