package com.arextest.report.model.mapper;

import com.arextest.report.model.dao.mongodb.MessagePreprocessCollection;
import com.arextest.report.model.dto.MessagePreprocessDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessagePreprocessMapper {
    MessagePreprocessMapper INSTANCE = Mappers.getMapper(MessagePreprocessMapper.class);

    MessagePreprocessDto dtoFromDao(MessagePreprocessCollection dao);
}
