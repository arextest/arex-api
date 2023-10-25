package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.filesystem.FSQueryFolderResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveFolderRequestType;
import com.arextest.web.model.dao.mongodb.FSFolderCollection;
import com.arextest.web.model.dto.filesystem.FSFolderDto;
import com.arextest.web.model.dto.filesystem.importexport.FolderItemDto;

@Mapper
public interface FSFolderMapper {
    FSFolderMapper INSTANCE = Mappers.getMapper(FSFolderMapper.class);

    FSFolderDto dtoFromContract(FSSaveFolderRequestType contract);

    FSQueryFolderResponseType contractFromDto(FSFolderDto dto);

    FSFolderDto dtoFromDao(FSFolderCollection dao);

    FSFolderCollection daoFromDto(FSFolderDto dto);

    FolderItemDto ieItemFromFsItemDto(FSFolderDto dto);

    FSFolderDto fsItemFromIeItemDto(FolderItemDto dto);
}
