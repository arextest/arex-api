package com.arextest.report.model.mapper;

import com.arextest.report.model.api.contracts.filesystem.FSQueryCaseResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.report.model.dao.mongodb.FSCaseCollection;
import com.arextest.report.model.dto.filesystem.FSCaseDto;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.dto.filesystem.importexport.CaseItemDto;
import com.arextest.report.model.dto.filesystem.importexport.InterfaceItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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
