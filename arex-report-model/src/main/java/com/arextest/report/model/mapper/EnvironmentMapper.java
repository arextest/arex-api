package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.environment.EnvironmentType;
import com.arextest.report.model.api.contracts.environment.SaveEnvironmentRequestType;
import com.arextest.report.model.dao.mongodb.EnvironmentCollection;
import com.arextest.report.model.dto.EnvironmentDto;
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
