package com.arextest.web.model.mapper;

import com.arextest.web.model.dao.mongodb.FSFolderCollection;
import com.arextest.web.model.dto.filesystem.FSFolderDto;
import com.arextest.web.model.dto.filesystem.importexport.FolderItemDto;
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
