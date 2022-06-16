package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.filesystem.WorkspaceType;
import com.arextest.report.model.dto.WorkspaceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface WorkspaceMapper {
    WorkspaceMapper INSTANCE = Mappers.getMapper(WorkspaceMapper.class);

    List<WorkspaceType> contractFromDtoList(List<WorkspaceDto> dtos);
}
