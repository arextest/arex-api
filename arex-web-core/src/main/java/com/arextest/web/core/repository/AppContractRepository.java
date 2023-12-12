package com.arextest.web.core.repository;

import com.arextest.web.model.dto.AppContractDto;
import java.util.List;

public interface AppContractRepository extends RepositoryProvider {

  boolean update(List<AppContractDto> appContractDtos);

  boolean upsert(AppContractDto appContractDto);

  List<AppContractDto> insert(List<AppContractDto> appContractDtos);

  List<AppContractDto> queryAppContractListByOpIds(List<String> operationList,
      List<String> filterFields);

  AppContractDto queryAppContractByType(String id, Integer contractType);

  AppContractDto queryById(String id);

  AppContractDto findAndModifyAppContract(AppContractDto appContractDto);

  AppContractDto queryDependency(String operationId, String operationType, String operationName);

  List<AppContractDto> queryAppContracts(String appId, String operationId);

  List<AppContractDto> queryDependencyWithAppId(String appId, String operationName,
      String operationType);
}
