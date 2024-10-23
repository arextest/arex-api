package com.arextest.web.core.business;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.model.mock.MockCategoryType;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.util.SchemaUtils;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.core.repository.ReplayCompareResultRepository;
import com.arextest.web.model.contract.contracts.OverwriteContractRequestType;
import com.arextest.web.model.contract.contracts.QueryContractRequestType;
import com.arextest.web.model.contract.contracts.SyncResponseContractRequestType;
import com.arextest.web.model.contract.contracts.SyncResponseContractResponseType;
import com.arextest.web.model.contract.contracts.common.DependencyWithContract;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.enums.ContractTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchemaInferService {

  private static final String NULL_STR = "null";
  private static final String EMPTY_CONTRACT = "{}";
  private static final int LIMIT = 5;
  private static final int FIRST_INDEX = 0;
  private static final ObjectMapper CONTRACT_OBJ_MAPPER = new ObjectMapper();
  private static final Set<String> EXCLUDE_OPERATION_TYPE = new HashSet<>(Arrays.asList(
      MockCategoryType.REDIS.getName(),
      MockCategoryType.DATABASE.getName()));

  @Resource
  private ReplayCompareResultRepository replayCompareResultRepository;
  @Resource
  private AppContractRepository appContractRepository;
  @Resource
  private ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

  public AppContractDto queryContract(QueryContractRequestType requestType) {
    if (requestType.getOperationType() != null || requestType.getOperationName() != null) {
      return appContractRepository.queryDependency(requestType.getOperationId(),
          requestType.getOperationType(),
          requestType.getOperationName());
    } else if (requestType.getOperationId() != null) {
      return appContractRepository.queryAppContractByType(requestType.getOperationId(),
          ContractTypeEnum.ENTRY.getCode());
    } else if (requestType.getAppId() != null) {
      return appContractRepository.queryAppContractByType(requestType.getAppId(),
          ContractTypeEnum.GLOBAL.getCode());
    }
    return null;
  }

  public List<AppContractDto> queryAllContracts(QueryContractRequestType requestType) {
    List<AppContractDto> appContractList = new ArrayList<>();
    if (requestType.getAppId() != null && requestType.getOperationId() != null) {
      List<AppContractDto> appContractDtos = appContractRepository.queryAppContracts(
              requestType.getAppId(),
              requestType.getOperationId())
          .stream()
          .filter(
              appContractDto -> !EXCLUDE_OPERATION_TYPE.contains(appContractDto.getOperationType()))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(appContractDtos)) {
        appContractList.addAll(appContractDtos);
      }
    }
    return appContractList;
  }

  public Set<String> queryFlatContract(QueryContractRequestType requestType) {
    AppContractDto appContractDto = queryContract(requestType);
    return SchemaUtils.getFlatContract(appContractDto.getContract());
  }

  public AppContractDto overwriteContract(OverwriteContractRequestType request) {
    AppContractDto appContractDto = new AppContractDto();
    appContractDto.setContract(
        SchemaUtils.mergeJson(EMPTY_CONTRACT, request.getOperationResponse()));
    appContractDto.setAppId(request.getAppId());
    appContractDto.setOperationId(request.getOperationId());

    if (request.getOperationType() != null || request.getOperationName() != null) {
      appContractDto.setOperationType(request.getOperationType());
      appContractDto.setOperationName(request.getOperationName());
      appContractDto.setContractType(ContractTypeEnum.DEPENDENCY.getCode());
    } else if (request.getOperationId() != null) {
      appContractDto.setContractType(ContractTypeEnum.ENTRY.getCode());
    } else if (request.getAppId() != null) {
      appContractDto.setContractType(ContractTypeEnum.GLOBAL.getCode());
    } else {
      return null;
    }
    return appContractRepository.findAndModifyAppContract(appContractDto);
  }

  public SyncResponseContractResponseType syncResponseContract(
      SyncResponseContractRequestType request) {
    String operationId = request.getOperationId();
    SyncResponseContractResponseType responseType = new SyncResponseContractResponseType();
    ApplicationOperationConfiguration applicationOperationConfiguration =
        applicationOperationConfigurationRepository.listByOperationId(operationId);
    Set<String> entryPointTypes = applicationOperationConfiguration.getOperationTypes();

    List<CompareResultDto> latestNCompareResults = replayCompareResultRepository
        .queryLatestCompareResultForEachType(operationId, LIMIT);

    List<CompareResultDto> latestNEntryCompareResults = latestNCompareResults.stream()
        .filter(compareResultDto -> entryPointTypes.contains(compareResultDto.getCategoryName()))
        .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(latestNCompareResults)) {
      return responseType;
    }
    List<String> planItemIds =
        latestNCompareResults.stream().map(CompareResultDto::getPlanItemId)
            .collect(Collectors.toList());
    List<String> recordIds =
        latestNCompareResults.stream().map(CompareResultDto::getRecordId)
            .collect(Collectors.toList());
    List<CompareResultDto> compareResultDtoList =
        replayCompareResultRepository.queryCompareResults(planItemIds, recordIds);

    // distinct by operationType-operationName
    Map<Pair<String, String>, List<CompareResultDto>> dependencyMap = new HashMap<>();
    for (CompareResultDto item : compareResultDtoList) {
      // filter dependency
      if (entryPointTypes.contains(item.getCategoryName())) {
        continue;
      }
      Pair<String, String> pair = new ImmutablePair<>(item.getCategoryName(),
          item.getOperationName());
      List<CompareResultDto> compareResultDtos = dependencyMap.getOrDefault(pair,
          new ArrayList<>());
      if (compareResultDtos.size() >= LIMIT) {
        continue;
      }
      compareResultDtos.add(item);
      dependencyMap.put(pair, compareResultDtos);
    }

    // entryPoint contract
    List<AppContractDto> upserts = new ArrayList<>();
    AppContractDto entryPointApplication = new AppContractDto();
    entryPointApplication.setOperationId(operationId);
    entryPointApplication.setContract(perceiveContract(latestNEntryCompareResults));
    entryPointApplication.setAppId(applicationOperationConfiguration.getAppId());
    entryPointApplication.setContractType(ContractTypeEnum.ENTRY.getCode());
    entryPointApplication.setOperationType(applicationOperationConfiguration.getOperationType());
    entryPointApplication.setOperationName(applicationOperationConfiguration.getOperationName());
    upserts.add(entryPointApplication);

    dependencyMap.values().forEach(list -> {
      CompareResultDto compareResultDto = list.get(FIRST_INDEX);
      if (CollectionUtils.isNotEmpty(list)) {
        AppContractDto dependencyApplication = new AppContractDto();
        dependencyApplication.setContract(perceiveContract(list));
        dependencyApplication.setOperationId(operationId);
        dependencyApplication.setOperationName(compareResultDto.getOperationName());
        dependencyApplication.setOperationType(compareResultDto.getCategoryName());
        dependencyApplication.setAppId(applicationOperationConfiguration.getAppId());
        dependencyApplication.setContractType(ContractTypeEnum.DEPENDENCY.getCode());
        upserts.add(dependencyApplication);
      }
    });

    List<AppContractDto> appContractDtoList =
        appContractRepository.queryAppContractListByOpIds(Collections.singletonList(operationId),
            null);
    // pair of <type,name>, entryPoint doesn't need type to identify
    Map<Pair<String, String>,
        AppContractDto> existedMap =
        appContractDtoList.stream()
            .collect(Collectors.toMap(
                item -> Objects.equals(item.getContractType(), ContractTypeEnum.ENTRY.getCode())
                    ? new ImmutablePair<>(null, null)
                    : new ImmutablePair<>(item.getOperationType(), item.getOperationName()),
                Function.identity()));
    // separate updates and inserts
    List<AppContractDto> updates = new ArrayList<>();
    List<AppContractDto> inserts = new ArrayList<>();
    Long currentTimeMillis = System.currentTimeMillis();
    for (AppContractDto item : upserts) {
      Pair<String,
          String> pair = Objects.equals(item.getContractType(), ContractTypeEnum.ENTRY.getCode())
          ? new ImmutablePair<>(null, null)
          : new ImmutablePair<>(item.getOperationType(), item.getOperationName());
      if (existedMap.containsKey(pair)) {
        String oldContract = existedMap.get(pair).getContract();
        // expand old contract but not overwrite simply
        if (!StringUtils.equals(oldContract, item.getContract()) && oldContract != null
            && !oldContract.equals(NULL_STR)) {
          String newContract = SchemaUtils.mergeJson(oldContract, item.getContract());
          item.setContract(newContract);
        }
        item.setId(existedMap.get(pair).getId());
        item.setDataChangeUpdateTime(currentTimeMillis);
        updates.add(item);
      } else {
        item.setDataChangeUpdateTime(currentTimeMillis);
        item.setDataChangeCreateTime(currentTimeMillis);
        inserts.add(item);
      }
    }
    if (CollectionUtils.isNotEmpty(updates)) {
      appContractRepository.update(updates);
    }
    if (CollectionUtils.isNotEmpty(inserts)) {
      List<AppContractDto> insertResults = new ArrayList<>(appContractRepository.insert(inserts));
      updates.addAll(insertResults);
    }

    List<DependencyWithContract> dependencyList = updates.stream()
        .filter(
            appContractDto -> !Objects.equals(appContractDto.getContractType(),
                ContractTypeEnum.ENTRY.getCode()))
        .map(this::buildDependency).collect(Collectors.toList());
    responseType.setEntryPointContractStr(entryPointApplication.getContract());
    responseType.setDependencyList(dependencyList);
    return responseType;
  }

  private DependencyWithContract buildDependency(AppContractDto appContractDto) {
    DependencyWithContract dependency = new DependencyWithContract();
    dependency.setDependencyId(appContractDto.getId());
    dependency.setOperationName(appContractDto.getOperationName());
    dependency.setOperationType(appContractDto.getOperationType());
    dependency.setContract(appContractDto.getContract());
    return dependency;
  }

  private String perceiveContract(List<CompareResultDto> compareResultDtoList) {
    Map<String, Object> contract = new HashMap<>();
    try {
      for (CompareResultDto compareResultDto : compareResultDtoList) {

        if (compareResultDto.getBaseMsg() != null) {
          try {
            SchemaUtils.mergeMap(contract,
                CONTRACT_OBJ_MAPPER.readValue(compareResultDto.getBaseMsg(), Map.class));
          } catch (JsonProcessingException e) {
            LogUtils.error(LOGGER, "ObjectMapper readValue failed, exception:{}, msg:{}", e,
                compareResultDto.getBaseMsg());
          }
        }
        if (compareResultDto.getTestMsg() != null) {
          try {
            SchemaUtils.mergeMap(contract,
                CONTRACT_OBJ_MAPPER.readValue(compareResultDto.getTestMsg(), Map.class));
          } catch (JsonProcessingException e) {
            LogUtils.error(LOGGER, "ObjectMapper readValue failed, exception:{}, msg:{}", e,
                compareResultDto.getTestMsg());
          }
        }
      }
      return CONTRACT_OBJ_MAPPER.writeValueAsString(contract);
    } catch (JsonProcessingException e2) {
      LogUtils.error(LOGGER, "ObjectMapper writeValue failed, exception:{}, contract:{}", e2,
          contract);
      return null;
    }
  }
}
