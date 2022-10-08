package com.arextest.report.model.mapper;

import com.arextest.report.model.dao.mongodb.FSFolderCollection;
import com.arextest.report.model.dto.filesystem.FSFolderDto;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.dto.filesystem.FSItemDto;
import com.arextest.report.model.dto.filesystem.importexport.FolderItemDto;
import com.arextest.report.model.dto.filesystem.importexport.InterfaceItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FSFolderMapper {
    FSFolderMapper INSTANCE = Mappers.getMapper(FSFolderMapper.class);

    FSFolderDto dtoFromDao(FSFolderCollection dao);

    FSFolderCollection daoFromDto(FSFolderDto dto);

    FolderItemDto ieItemFromFsItemDto(FSFolderDto dto);

    FSFolderDto fsItemFromIeItemDto(FolderItemDto dto);
}
