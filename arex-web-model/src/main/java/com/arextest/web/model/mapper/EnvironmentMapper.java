package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.environment.EnvironmentType;
import com.arextest.web.model.dao.mongodb.EnvironmentCollection;
import com.arextest.web.model.dto.EnvironmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EnvironmentMapper {
    EnvironmentMapper INSTANCE = Mappers.getMapper(EnvironmentMapper.class);

    EnvironmentCollection daoFromDto(EnvironmentDto dto);

    EnvironmentDto dtoFromDao(EnvironmentCollection dao);

    List<EnvironmentDto> dtoFromDaoList(List<EnvironmentCollection> dao);

    EnvironmentDto dtoFromContract(EnvironmentType contract);

    List<EnvironmentType> contractFromDtoList(List<EnvironmentDto> dto);
}
