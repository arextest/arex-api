package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.filesystem.FSQueryInterfaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveInterfaceRequestType;
import com.arextest.report.model.dao.mongodb.FSInterfaceCollection;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FSInterfaceMapper {
    FSInterfaceMapper INSTANCE = Mappers.getMapper(FSInterfaceMapper.class);

    FSInterfaceDto dtoFromContract(FSSaveInterfaceRequestType contract);

    FSQueryInterfaceResponseType contractFromDto(FSInterfaceDto dto);

    FSInterfaceCollection daoFromDto(FSInterfaceDto dto);

    FSInterfaceDto dtoFromDao(FSInterfaceCollection dao);
}
