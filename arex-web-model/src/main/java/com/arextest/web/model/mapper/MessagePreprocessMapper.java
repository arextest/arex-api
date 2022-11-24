package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.MessagePreprocessCollection;
import com.arextest.web.model.dto.MessagePreprocessDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessagePreprocessMapper {
    MessagePreprocessMapper INSTANCE = Mappers.getMapper(MessagePreprocessMapper.class);

    MessagePreprocessDto dtoFromDao(MessagePreprocessCollection dao);
}
