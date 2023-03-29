package com.arextest.web.model.mapper;

import com.arextest.web.model.contract.contracts.filesystem.FSQueryCaseResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.web.model.dao.mongodb.FSCaseCollection;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.importexport.CaseItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FSCaseMapper {
    FSCaseMapper INSTANCE = Mappers.getMapper(FSCaseMapper.class);

    FSCaseDto dtoFromContract(FSSaveCaseRequestType contract);

    FSQueryCaseResponseType contractFromDto(FSCaseDto dto);

    FSCaseCollection daoFromDto(FSCaseDto dto);

    // @Mappings({
    //         @Mapping(target = "inherited", expression = "java(dao.getInherited() != null ? dao.getInherited() : Boolean.TRUE)"),
    // })
    FSCaseDto dtoFromDao(FSCaseCollection dao);

    CaseItemDto ieItemFromFsItemDto(FSCaseDto dto);

    FSCaseDto fsItemFromIeItemDto(CaseItemDto dto);
}