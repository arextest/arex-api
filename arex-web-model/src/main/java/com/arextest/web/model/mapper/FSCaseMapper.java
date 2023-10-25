package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.filesystem.FSQueryCaseResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.web.model.dao.mongodb.FSCaseCollection;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.importexport.CaseItemDto;

@Mapper
public interface FSCaseMapper {
    FSCaseMapper INSTANCE = Mappers.getMapper(FSCaseMapper.class);

    FSCaseDto dtoFromContract(FSSaveCaseRequestType contract);

    FSQueryCaseResponseType contractFromDto(FSCaseDto dto);

    FSCaseCollection daoFromDto(FSCaseDto dto);

    FSCaseDto dtoFromDao(FSCaseCollection dao);

    CaseItemDto ieItemFromFsItemDto(FSCaseDto dto);

    FSCaseDto fsItemFromIeItemDto(CaseItemDto dto);
}