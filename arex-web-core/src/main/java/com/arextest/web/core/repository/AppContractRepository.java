package com.arextest.web.core.repository;

import com.arextest.web.model.dto.AppContractDto;

import java.util.List;

public interface AppContractRepository extends RepositoryProvider {

    boolean saveAppContractList(List<AppContractDto> applicationInfoDtos);

    List<AppContractDto> queryAppContractList(String operationId);
}
