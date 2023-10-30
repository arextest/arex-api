package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.filesystem.FSQueryInterfaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveInterfaceRequestType;
import com.arextest.web.model.dao.mongodb.FSInterfaceCollection;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.importexport.InterfaceItemDto;

@Mapper
public interface FSInterfaceMapper {
    FSInterfaceMapper INSTANCE = Mappers.getMapper(FSInterfaceMapper.class);

    FSInterfaceDto dtoFromContract(FSSaveInterfaceRequestType contract);

    FSQueryInterfaceResponseType contractFromDto(FSInterfaceDto dto);

    FSInterfaceCollection daoFromDto(FSInterfaceDto dto);

    FSInterfaceDto dtoFromDao(FSInterfaceCollection dao);

    InterfaceItemDto ieItemFromFsItemDto(FSInterfaceDto dto);

    FSInterfaceDto fsItemFromIeItemDto(InterfaceItemDto dto);
}
