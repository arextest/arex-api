package com.arextest.report.model.mapper;

import com.arextest.report.model.dao.mongodb.ServletMockerCollection;
import com.arextest.report.model.dto.ServletMockerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServletMockerMapper {
    ServletMockerMapper INSTANCE = Mappers.getMapper(ServletMockerMapper.class);

    ServletMockerDto dtoFromDao(ServletMockerCollection dao);
}
