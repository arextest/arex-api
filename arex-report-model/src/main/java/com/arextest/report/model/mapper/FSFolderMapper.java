package com.arextest.report.model.mapper;

import com.arextest.report.model.dao.mongodb.FSFolderCollection;
import com.arextest.report.model.dto.filesystem.FSFolderDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FSFolderMapper extends FSItemMapper {
    FSFolderMapper INSTANCE = Mappers.getMapper(FSFolderMapper.class);

    FSFolderDto dtoFromDao(FSFolderCollection dao);

    FSFolderCollection daoFromDto(FSFolderDto dto);
}
