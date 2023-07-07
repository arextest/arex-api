package com.arextest.web.core.repository;

import com.arextest.web.model.dto.AppContractDto;

import java.util.List;

public interface AppContractRepository extends RepositoryProvider {

    boolean updateById(List<AppContractDto> appContractDtos);

    List<AppContractDto> insert(List<AppContractDto> appContractDtos);

    List<AppContractDto> queryAppContractListByOpId(String operationId);

    AppContractDto queryEntryPointContract(String operationId);

    AppContractDto queryById(String id);
}
