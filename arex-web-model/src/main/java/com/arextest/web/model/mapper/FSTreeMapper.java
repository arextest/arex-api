package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSTreeType;
import com.arextest.web.model.dao.mongodb.FSTreeCollection;
import com.arextest.web.model.dto.filesystem.FSTreeDto;

@Mapper
public interface FSTreeMapper {
    FSTreeMapper INSTANCE = Mappers.getMapper(FSTreeMapper.class);

    FSTreeDto dtoFromDao(FSTreeCollection dao);

    FSTreeCollection daoFromDto(FSTreeDto dto);

    FSTreeType contractFromDto(FSTreeDto dto);

    FSTreeDto dtoFromContract(FSAddWorkspaceRequestType contract);
}
