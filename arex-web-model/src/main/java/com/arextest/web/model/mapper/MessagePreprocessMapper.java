package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.dao.mongodb.MessagePreprocessCollection;
import com.arextest.web.model.dto.MessagePreprocessDto;

@Mapper
public interface MessagePreprocessMapper {
    MessagePreprocessMapper INSTANCE = Mappers.getMapper(MessagePreprocessMapper.class);

    MessagePreprocessDto dtoFromDao(MessagePreprocessCollection dao);
}
