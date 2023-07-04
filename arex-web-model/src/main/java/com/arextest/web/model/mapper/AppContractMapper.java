package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AppContractMapper extends BaseMapper {
    AppContractMapper INSTANCE = Mappers.getMapper(AppContractMapper.class);

    AppContractCollection daoFromDto(AppContractDto dto);

    AppContractDto dtoFromDao(AppContractCollection dao);
}
