package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.PreprocessConfigCollection;
import com.arextest.web.model.dto.PreprocessConfigDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PreprocessConfigMapper {
    PreprocessConfigMapper INSTANCE = Mappers.getMapper(PreprocessConfigMapper.class);

    PreprocessConfigDto dtoFromDao(PreprocessConfigCollection dao);
}
