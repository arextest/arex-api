package com.arextest.web.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.environment.EnvironmentType;
import com.arextest.web.model.dao.mongodb.EnvironmentCollection;
import com.arextest.web.model.dto.EnvironmentDto;

@Mapper
public interface EnvironmentMapper {
    EnvironmentMapper INSTANCE = Mappers.getMapper(EnvironmentMapper.class);

    EnvironmentCollection daoFromDto(EnvironmentDto dto);

    EnvironmentDto dtoFromDao(EnvironmentCollection dao);

    List<EnvironmentDto> dtoFromDaoList(List<EnvironmentCollection> dao);

    EnvironmentDto dtoFromContract(EnvironmentType contract);

    List<EnvironmentType> contractFromDtoList(List<EnvironmentDto> dto);
}
