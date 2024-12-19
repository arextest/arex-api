package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.Dependency;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.model.contract.contracts.config.replay.AbstractComparisonDetailsConfiguration;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.dto.config.PageQueryComparisonDto;
import com.arextest.web.model.enums.ContractTypeEnum;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jmo
 * @since 2022/1/22
 */
public abstract class AbstractComparisonConfigurableHandler<T extends AbstractComparisonDetailsConfiguration>
    extends AbstractConfigurableHandler<T> {

  private AppContractRepository appContractRepository;

  protected AbstractComparisonConfigurableHandler(ConfigRepositoryProvider<T> repositoryProvider,
      AppContractRepository appContractRepository) {
    super(repositoryProvider);
    this.appContractRepository = appContractRepository;
  }

  @Override
  public List<T> useResultAsList(String appId) {
    return repositoryProvider.listBy(appId);
  }

  public List<T> useResultAsList(String appId, String operationId) {
    return repositoryProvider.listBy(appId, operationId);
  }

  public List<T> useResultAsList(String appId, int compareConfigType) {
    return this.useResultAsList(appId).stream()
        .filter(config -> config.getCompareConfigType() == compareConfigType)
        .collect(Collectors.toList());
  }

  public List<T> queryByOperationIdAndInterfaceId(String interfaceId, String operationId) {
    return repositoryProvider.queryByInterfaceIdAndOperationId(interfaceId, operationId);
  }

  public abstract List<T> queryByInterfaceId(String interfaceId);

  public List<T> queryComparisonConfig(String appId, String operationId, String operationType,
      String operationName) {

    // query the config of dependency
    if (operationType != null || operationName != null) {
      AppContractDto appContractDto =
          appContractRepository.queryDependency(operationId, operationType, operationName);
      if (appContractDto == null) {
        return Collections.emptyList();
      }

      List<T> comparisonConfigList = this.useResultAsList(appId, operationId);
      return comparisonConfigList.stream()
          .filter(config -> Objects.equals(config.getDependencyId(), appContractDto.getId()))
          .peek(item -> {
            item.setOperationType(operationType);
            item.setOperationName(operationName);
          }).collect(Collectors.toList());
    }

    // query the config of operation
    if (operationId != null) {
      List<T> comparisonConfigList = this.useResultAsList(appId, operationId);
      return comparisonConfigList.stream()
          .filter(config -> Objects.equals(config.getDependencyId(), null))
          .collect(Collectors.toList());
    }

    // query the config of app global
    return this.useResultAsList(appId, null);
  }

  public void removeDetailsExpired(List<T> comparisonDetails, Boolean filterExpired) {
    if (CollectionUtils.isNotEmpty(comparisonDetails) && Boolean.TRUE.equals(filterExpired)) {
      comparisonDetails.removeIf(T::expired);
    }
  }

  @Override
  public boolean insert(T comparisonDetail) {
    return this.insertList(Collections.singletonList(comparisonDetail));
  }

  @Override
  public boolean insertList(List<T> configurationList) {
    List<T> configurations = Optional.ofNullable(configurationList).map(List::stream)
        .orElse(Stream.empty())
        .filter(item -> item != null && StringUtils.isNotEmpty(item.getAppId())).peek(item -> {
          if (item.getExpirationDate() == null) {
            item.setExpirationDate(new Date());
          }
        }).collect(Collectors.toList());
    this.addDependencyId(configurations);
    return repositoryProvider.insertList(configurations);
  }

  public boolean removeByAppId(String appId) {
    return repositoryProvider.listBy(appId).isEmpty() || repositoryProvider.removeByAppId(appId);
  }


  protected Map<String, String> getOperationInfos(
      List<T> configs,
      ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository) {
    Map<String, String> operationInfos = new HashMap<>();
    for (T item : configs) {
      operationInfos.put(item.getOperationId(), null);
    }
    List<ApplicationOperationConfiguration> operationConfigurations =
        applicationOperationConfigurationRepository.queryByOperationIdList(operationInfos.keySet());
    for (ApplicationOperationConfiguration operationConfiguration : operationConfigurations) {
      operationInfos.put(operationConfiguration.getId(), operationConfiguration.getOperationName());
    }
    return operationInfos;
  }

  protected Map<String, Dependency> getDependencyInfos(
      List<T> configs,
      AppContractRepository appContractRepository) {
    Map<String, Dependency> dependencyInfos = new HashMap<>();
    for (T item : configs) {
      dependencyInfos.put(item.getDependencyId(), null);
    }
    List<AppContractDto> appContractDtos = appContractRepository.queryAppContractsByIds(
        dependencyInfos.keySet());
    for (AppContractDto appContractDto : appContractDtos) {
      Dependency dependency = new Dependency();
      dependency.setDependencyId(appContractDto.getId());
      dependency.setOperationType(appContractDto.getOperationType());
      dependency.setOperationName(appContractDto.getOperationName());
      dependencyInfos.put(appContractDto.getId(), dependency);
    }
    return dependencyInfos;
  }


  void addDependencyId(List<T> comparisonDetails) {
    Map<AppContractDto, String> notFoundAppContractMap = new HashMap<>();
    for (T comparisonDetail : comparisonDetails) {
      if (comparisonDetail.getOperationType() == null
          && comparisonDetail.getOperationName() == null) {
        continue;
      }

      if (comparisonDetail.getDependencyId() != null) {
        continue;
      }

      AppContractDto appContractDto = new AppContractDto();
      appContractDto.setAppId(comparisonDetail.getAppId());
      appContractDto.setOperationId(comparisonDetail.getOperationId());
      appContractDto.setOperationType(comparisonDetail.getOperationType());
      appContractDto.setOperationName(comparisonDetail.getOperationName());
      appContractDto.setContractType(ContractTypeEnum.DEPENDENCY.getCode());
      if (notFoundAppContractMap.containsKey(appContractDto)) {
        comparisonDetail.setDependencyId(notFoundAppContractMap.get(appContractDto));
      } else {
        AppContractDto andModifyAppContract = appContractRepository.findAndModifyAppContract(
            appContractDto);
        String dependencyId = andModifyAppContract.getId();
        notFoundAppContractMap.put(appContractDto, dependencyId);
        comparisonDetail.setDependencyId(dependencyId);
      }
    }
  }

  protected void queryIdsByKeywords(PageQueryComparisonDto dto,
      ApplicationOperationConfigurationRepositoryImpl serviceOperationRepository) {
    if (StringUtils.isNotBlank(dto.getKeyOfOperationName())) {
      List<ApplicationOperationConfiguration> serviceOperations = serviceOperationRepository.queryLikeOperationName(
          dto.getAppId(), dto.getKeyOfOperationName());
      if (CollectionUtils.isNotEmpty(serviceOperations)) {
        dto.setOperationIds(serviceOperations.stream().map(ApplicationOperationConfiguration::getId)
            .collect(Collectors.toList()));
      }
    }

    if (StringUtils.isNotBlank(dto.getKeyOfDependencyName())) {
      List<AppContractDto> contractDtos = appContractRepository.queryLikeOperationName(
          dto.getAppId(),
          dto.getKeyOfDependencyName());
      if (CollectionUtils.isNotEmpty(contractDtos)) {
        dto.setDependencyIds(
            contractDtos.stream().map(AppContractDto::getId).collect(Collectors.toList()));
      }
    }
  }
}
