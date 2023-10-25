package com.arextest.web.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.filesystem.WorkspaceType;
import com.arextest.web.model.dto.WorkspaceDto;

@Mapper
public interface WorkspaceMapper {
    WorkspaceMapper INSTANCE = Mappers.getMapper(WorkspaceMapper.class);

    List<WorkspaceType> contractFromDtoList(List<WorkspaceDto> dtos);
}
