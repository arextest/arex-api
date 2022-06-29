package com.arextest.report.model.mapper;

import com.arextest.report.model.dao.mongodb.PreprocessConfigCollection;
import com.arextest.report.model.dto.PreprocessConfigDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PreprocessConfigMapper {
    PreprocessConfigMapper INSTANCE = Mappers.getMapper(PreprocessConfigMapper.class);

    PreprocessConfigDto dtoFromDao(PreprocessConfigCollection dao);
}
