package com.arextest.web.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.arextest.web.model.contract.contracts.appcontract.AddDependencyToSystemRequestType;
import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;

@Mapper
public interface AppContractMapper extends BaseMapper {
    AppContractMapper INSTANCE = Mappers.getMapper(AppContractMapper.class);

    AppContractCollection daoFromDto(AppContractDto dto);

    AppContractDto dtoFromDao(AppContractCollection dao);

    AppContractDto dtoFromContract(AddDependencyToSystemRequestType request);
}
