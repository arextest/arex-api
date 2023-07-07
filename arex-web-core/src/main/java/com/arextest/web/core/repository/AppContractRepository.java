package com.arextest.web.core.repository;

import com.arextest.web.model.dto.AppContractDto;

import java.util.List;

public interface AppContractRepository extends RepositoryProvider {

    boolean upsertAppContractList(List<AppContractDto> appContractDtos);

    boolean updateById(List<AppContractDto> appContractDtos);

    List<AppContractDto> upsertAppContractListWithResult(List<AppContractDto> appContractDtos);

    List<AppContractDto> queryAppContractList(String operationId);

    AppContractDto queryById(String id);
}
