package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.ServletMockerCollection;
import com.arextest.web.model.dto.ServletMockerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServletMockerMapper {
    ServletMockerMapper INSTANCE = Mappers.getMapper(ServletMockerMapper.class);

    ServletMockerDto dtoFromDao(ServletMockerCollection dao);
}
