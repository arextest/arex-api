package com.arextest.web.core.business.config.replay;

import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.ApplicationServiceConfiguration;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.model.contract.contracts.common.enums.ExpirationType;
import com.arextest.web.model.contract.contracts.config.replay.AbstractComparisonDetailsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonEncryptionConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonSummaryConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ReplayCompareConfig;
import com.arextest.web.model.contract.contracts.config.replay.ReplayCompareConfig.DependencyComparisonItem;
import com.arextest.web.model.contract.contracts.config.replay.ReplayCompareConfig.ReplayComparisonItem;
import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.enums.ContractTypeEnum;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Slf4j
public class ComparisonSummaryService {

  protected ComparisonExclusionsConfigurableHandler exclusionsConfigurableHandler;
  protected ComparisonInclusionsConfigurableHandler inclusionsConfigurableHandler;
  protected ComparisonEncryptionConfigurableHandler encryptionConfigurableHandler;
  protected ComparisonReferenceConfigurableHandler referenceConfigurableHandler;
  protected ComparisonListSortConfigurableHandler listSortConfigurableHandler;
  protected ComparisonIgnoreCategoryConfigurableHandler ignoreCategoryConfigurableHandler;
  protected ConfigurableHandler<ApplicationServiceConfiguration> applicationServiceConfigurationConfigurableHandler;
  protected AppContractRepository appContractRepository;

  public ComparisonSummaryService(
      @Autowired ComparisonExclusionsConfigurableHandler exclusionsConfigurableHandler,
      @Autowired ComparisonInclusionsConfigurableHandler inclusionsConfigurableHandler,
      @Autowired ComparisonEncryptionConfigurableHandler encryptionConfigurableHandler,
      @Autowired ComparisonReferenceConfigurableHandler referenceConfigurableHandler,
      @Autowired ComparisonListSortConfigurableHandler listSortConfigurableHandler,
      @Autowired ConfigurableHandler<
          ApplicationServiceConfiguration> applicationServiceConfigurationConfigurableHandler,
      @Autowired AppContractRepository appContractRepository,
      @Autowired ComparisonIgnoreCategoryConfigurableHandler ignoreCategoryConfigurableHandler) {
    this.exclusionsConfigurableHandler = exclusionsConfigurableHandler;
    this.inclusionsConfigurableHandler = inclusionsConfigurableHandler;
    this.encryptionConfigurableHandler = encryptionConfigurableHandler;
    this.referenceConfigurableHandler = referenceConfigurableHandler;
    this.listSortConfigurableHandler = listSortConfigurableHandler;
    this.applicationServiceConfigurationConfigurableHandler = applicationServiceConfigurationConfigurableHandler;
    this.appContractRepository = appContractRepository;
    this.ignoreCategoryConfigurableHandler = ignoreCategoryConfigurableHandler;
  }

  public ComparisonSummaryConfiguration getComparisonDetailsSummary(String interfaceId) {
    ComparisonSummaryConfiguration comparisonSummaryConfiguration = new ComparisonSummaryConfiguration();
    getComparisonExclusionsConfiguration(interfaceId, comparisonSummaryConfiguration);
    getComparisonInclusionsConfiguration(interfaceId, comparisonSummaryConfiguration);
    getComparisonListSortConfiguration(interfaceId, comparisonSummaryConfiguration);
    getComparisonReferenceConfiguration(interfaceId, comparisonSummaryConfiguration);
    getAdditionalComparisonConfiguration(interfaceId, comparisonSummaryConfiguration);
    return comparisonSummaryConfiguration;
  }

  public ReplayCompareConfig getReplayComparisonConfig(String appId) {
    ReplayCompareConfig result = new ReplayCompareConfig();
    // store all config items, key is operationId and null, the null for global config
    Map<String, ReplayCompareConfig.ReplayComparisonItem> replayComparisonItemMap;

    // build operation config and global config
    AppOperationAndDependencyInfo appOperationAndDependencyInfo = this.getOperationInfos(appId);
    Map<String, ApplicationOperationConfiguration> operationInfoMap =
        appOperationAndDependencyInfo.getOperationMap();
    Map<String, Map<String, AppContractDto>> appContractDtoMap =
        appOperationAndDependencyInfo.getAppContractDtoMap();
    replayComparisonItemMap = this.buildMultiConfiguration(appId, operationInfoMap,
        appContractDtoMap);

    // merge global config to all operation config
    this.mergeGlobalComparisonConfig(replayComparisonItemMap, operationInfoMap, appContractDtoMap);

    // build global config
    ReplayCompareConfig.GlobalComparisonItem globalComparisonItem = new ReplayCompareConfig.GlobalComparisonItem();
    ReplayCompareConfig.ReplayComparisonItem replayComparisonItem = replayComparisonItemMap.get(
        null);
    if (replayComparisonItem != null) {
      BeanUtils.copyProperties(replayComparisonItem, globalComparisonItem);
      result.setGlobalComparisonItem(globalComparisonItem);
    }

    ReplayComparisonItem globalReplayComparison = replayComparisonItemMap.get(null);
    List<ReplayComparisonItem> operationReplayComparison = replayComparisonItemMap.entrySet()
        .stream()
        .filter(item -> Objects.nonNull(item.getKey())).map(Entry::getValue)
        .collect(Collectors.toList());
    result.setReplayComparisonItems(operationReplayComparison);
    this.setDefaultWhenMissingDependency(globalReplayComparison, result);
    return result;
  }

  protected void getComparisonExclusionsConfiguration(String interfaceId,
      ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
    Set<List<String>> exclusionSet = new HashSet<>();
    List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurationList =
        exclusionsConfigurableHandler.queryByInterfaceId(interfaceId);
    Optional.ofNullable(comparisonExclusionsConfigurationList).orElse(Collections.emptyList())
        .forEach(item -> {
          exclusionSet.add(item.getExclusions());
        });
    comparisonSummaryConfiguration.setExclusionList(exclusionSet);
  }

  protected void getComparisonInclusionsConfiguration(String interfaceId,
      ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
    Set<List<String>> inclusionSet = new HashSet<>();
    List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurationList =
        inclusionsConfigurableHandler.queryByInterfaceId(interfaceId);
    Optional.ofNullable(comparisonInclusionsConfigurationList).orElse(Collections.emptyList())
        .forEach(item -> {
          inclusionSet.add(item.getInclusions());
        });
    comparisonSummaryConfiguration.setInclusionList(inclusionSet);
  }

  protected void getComparisonListSortConfiguration(String interfaceId,
      ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
    Map<List<String>, List<List<String>>> listSortMap = new HashMap<>();
    List<ComparisonListSortConfiguration> comparisonListSortConfigurationList =
        listSortConfigurableHandler.queryByInterfaceId(interfaceId);
    Optional.ofNullable(comparisonListSortConfigurationList).orElse(Collections.emptyList())
        .forEach(item -> {
          if (CollectionUtils.isNotEmpty(item.getListPath())) {
            listSortMap.put(item.getListPath(), item.getKeys());
          }
        });
    comparisonSummaryConfiguration.setListSortMap(listSortMap);
  }

  protected void getComparisonReferenceConfiguration(String interfaceId,
      ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
    Map<List<String>, List<String>> referenceMap = new HashMap<>();
    List<ComparisonReferenceConfiguration> comparisonReferenceConfigurationList =
        referenceConfigurableHandler.queryByInterfaceId(interfaceId);
    Optional.ofNullable(comparisonReferenceConfigurationList).orElse(Collections.emptyList())
        .forEach(item -> {
          if (CollectionUtils.isNotEmpty(item.getFkPath())) {
            referenceMap.put(item.getFkPath(), item.getPkPath());
          }
        });
    comparisonSummaryConfiguration.setReferenceMap(referenceMap);
  }

  protected void getAdditionalComparisonConfiguration(String interfaceId,
      ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
  }

  protected Map<String, ReplayCompareConfig.ReplayComparisonItem> buildMultiConfiguration(
      String appId,
      Map<String, ApplicationOperationConfiguration> operationInfoMap,
      Map<String, Map<String, AppContractDto>> appContractDtoMap) {

    Map<String, ReplayCompareConfig.ReplayComparisonItem> replayConfigurationMap = new HashMap<>();

    buildComparisonConfig(replayConfigurationMap,
        exclusionsConfigurableHandler.useResultAsList(appId),
        (configurations, summaryConfiguration) -> {
          Set<List<String>> operationExclusion = configurations.stream()
              .filter(config ->
                  config.getExpirationType() == ExpirationType.PINNED_NEVER_EXPIRED.getCodeValue()
                      || config.getExpirationDate().getTime() > System.currentTimeMillis())
              .map(ComparisonExclusionsConfiguration::getExclusions).collect(Collectors.toSet());
          summaryConfiguration.setExclusionList(operationExclusion);
        }, operationInfoMap, appContractDtoMap);

    buildComparisonConfig(replayConfigurationMap,
        ignoreCategoryConfigurableHandler.useResultAsList(appId),
        (configurations, summaryConfiguration) -> {
          List<String> ignoreCategoryTypes = configurations.stream()
              .map(ComparisonIgnoreCategoryConfiguration::getIgnoreCategory)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
          summaryConfiguration.setIgnoreCategoryTypes(ignoreCategoryTypes);
        }, operationInfoMap, appContractDtoMap);

    buildComparisonConfig(replayConfigurationMap,
        inclusionsConfigurableHandler.useResultAsList(appId),
        (configurations, summaryConfiguration) -> {
          Set<List<String>> operationInclusion = configurations.stream()
              .map(ComparisonInclusionsConfiguration::getInclusions).collect(Collectors.toSet());
          summaryConfiguration.setInclusionList(operationInclusion);
        }, operationInfoMap, appContractDtoMap);

    buildComparisonConfig(replayConfigurationMap,
        encryptionConfigurableHandler.useResultAsList(appId),
        (configurations, summaryConfiguration) -> {
          Set<List<String>> operationEncryption =
              configurations.stream().map(ComparisonEncryptionConfiguration::getPath)
                  .collect(Collectors.toSet());
          summaryConfiguration.setEncryptionList(operationEncryption);
        }, operationInfoMap, appContractDtoMap);

    buildComparisonConfig(replayConfigurationMap,
        listSortConfigurableHandler.useResultAsList(appId),
        (configurations, summaryConfiguration) -> {
          Map<List<String>,
              List<List<String>>> operationListSortMap = configurations.stream()
              .filter(item -> CollectionUtils.isNotEmpty(item.getListPath())
                  && CollectionUtils.isNotEmpty(item.getKeys()))
              .collect(Collectors.toMap(ComparisonListSortConfiguration::getListPath,
                  ComparisonListSortConfiguration::getKeys, (r1, r2) -> {
                    LogUtils.warn(LOGGER, "listSort duplicate key",
                        ImmutableMap.of("appId", appId));
                    return r2;
                  }));
          summaryConfiguration.setListSortMap(operationListSortMap);
        }, operationInfoMap, appContractDtoMap);

    buildComparisonConfig(replayConfigurationMap,
        referenceConfigurableHandler.useResultAsList(appId),
        (configurations, summaryConfiguration) -> {
          Map<List<String>,
              List<String>> operationReferenceMap = configurations.stream()
              .filter(item -> CollectionUtils.isNotEmpty(item.getFkPath())
                  && CollectionUtils.isNotEmpty(item.getPkPath()))
              .collect(Collectors.toMap(ComparisonReferenceConfiguration::getFkPath,
                  ComparisonReferenceConfiguration::getPkPath, (r1, r2) -> {
                    LogUtils.warn(LOGGER, "reference duplicate key",
                        ImmutableMap.of("appId", appId));
                    return r2;
                  }));
          summaryConfiguration.setReferenceMap(operationReferenceMap);
        }, operationInfoMap, appContractDtoMap);
    return replayConfigurationMap;
  }

  /**
   * get the info of operation key: operationId value: operationType
   *
   * @param appId appId
   * @return Map<String, ApplicationOperationConfiguration>
   */
  protected AppOperationAndDependencyInfo getOperationInfos(String appId) {
    AppOperationAndDependencyInfo appOperationAndDependencyInfo = new AppOperationAndDependencyInfo();

    Map<String, ApplicationOperationConfiguration> operationMap = new HashMap<>();
    List<ApplicationServiceConfiguration> applicationServiceConfigurations =
        applicationServiceConfigurationConfigurableHandler.useResultAsList(appId);
    Optional.ofNullable(applicationServiceConfigurations).orElse(Collections.emptyList())
        .forEach(item -> {
          List<ApplicationOperationConfiguration> operationList = item.getOperationList();
          if (CollectionUtils.isNotEmpty(operationList)) {
            for (ApplicationOperationConfiguration operationConfiguration : operationList) {
              operationMap.put(operationConfiguration.getId(), operationConfiguration);
            }
          }
        });

    List<String> operationIdList = new ArrayList<>(operationMap.keySet());
    // appContractDtoList filter the operation and group by operationId
    List<AppContractDto> appContractDtos = appContractRepository.queryAppContractListByOpIds(
        operationIdList,
        Collections.singletonList(AppContractCollection.Fields.contract));
    Map<String,
        Map<String, AppContractDto>> appContractDtoMap = appContractDtos.stream()
        .filter(
            item -> Objects.equals(item.getContractType(), ContractTypeEnum.DEPENDENCY.getCode()))
        .collect(Collectors.groupingBy(AppContractDto::getOperationId,
            Collectors.toMap(AppContractDto::getId, Function.identity())));

    appOperationAndDependencyInfo.setOperationMap(operationMap);
    appOperationAndDependencyInfo.setAppContractDtoMap(appContractDtoMap);
    return appOperationAndDependencyInfo;
  }

  protected <T extends AbstractComparisonDetailsConfiguration> void buildComparisonConfig(
      Map<String, ReplayCompareConfig.ReplayComparisonItem> replayConfigurationMap,
      List<T> configurations,
      BiConsumer<List<T>, ComparisonSummaryConfiguration> assignFunction,
      Map<String, ApplicationOperationConfiguration> operationMap,
      Map<String, Map<String, AppContractDto>> appContractDtoMap) {

    if (CollectionUtils.isNotEmpty(configurations)) {
      // comparisonExclusionsConfigurations group by operationId
      Map<String, List<T>> configurationsMap = new HashMap<>();
      for (T config : configurations) {
        configurationsMap.computeIfAbsent(config.getOperationId(), k -> new ArrayList<>())
            .add(config);
      }

      for (Map.Entry<String, List<T>> entry : configurationsMap.entrySet()) {

        String operationId = entry.getKey();
        List<T> configurationList = entry.getValue();

        ReplayCompareConfig.ReplayComparisonItem tempReplayComparisonItem =
            replayConfigurationMap.getOrDefault(operationId,
                new ReplayCompareConfig.ReplayComparisonItem());
        tempReplayComparisonItem.setOperationId(operationId);
        ApplicationOperationConfiguration operationConfiguration = operationMap.get(operationId);
        if (operationConfiguration != null) {
          tempReplayComparisonItem
              .setOperationTypes(new ArrayList<>(operationConfiguration.getOperationTypes()));
          tempReplayComparisonItem.setOperationName(operationConfiguration.getOperationName());
        }
        // configurationList group by dependencyId if key is null, it means the configuration of
        // operation. if key is not null, it means the configuration of dependency.
        Map<String, List<T>> dependencyConfigurationsMap = new HashMap<>();
        for (T config : configurationList) {
          dependencyConfigurationsMap.computeIfAbsent(config.getDependencyId(),
                  k -> new ArrayList<>())
              .add(config);
        }

        // build the configuration of operation
        List<T> operationConfig = dependencyConfigurationsMap.getOrDefault(null,
            Collections.emptyList());
        assignFunction.accept(operationConfig, tempReplayComparisonItem);

        // build the configuration of dependency
        Map<String, ReplayCompareConfig.DependencyComparisonItem> dependencyItemMap =
            Optional.ofNullable(tempReplayComparisonItem.getDependencyComparisonItems())
                .orElse(Collections.emptyList()).stream().collect(Collectors
                    .toMap(ReplayCompareConfig.DependencyComparisonItem::getDependencyId,
                        Function.identity()));

        Map<String, AppContractDto> dependencyContractMap =
            Optional.ofNullable(appContractDtoMap.get(operationId)).orElse(Collections.emptyMap());

        for (Map.Entry<String, List<T>> dependencyEntry : dependencyConfigurationsMap.entrySet()) {
          String dependencyId = dependencyEntry.getKey();
          if (dependencyId == null) {
            continue;
          }
          AppContractDto dependencyContract = dependencyContractMap.get(dependencyId);
          if (dependencyContract == null) {
            continue;
          }
          List<T> dependencyConfig = dependencyEntry.getValue();
          if (dependencyItemMap.containsKey(dependencyId)) {
            ReplayCompareConfig.DependencyComparisonItem dependencyComparisonItem =
                dependencyItemMap.get(dependencyId);
            assignFunction.accept(dependencyConfig, dependencyComparisonItem);
          } else {
            ReplayCompareConfig.DependencyComparisonItem dependencyComparisonItem =
                new ReplayCompareConfig.DependencyComparisonItem();
            assignFunction.accept(dependencyConfig, dependencyComparisonItem);
            dependencyComparisonItem.setDependencyId(dependencyId);
            dependencyComparisonItem.setOperationName(dependencyContract.getOperationName());
            dependencyComparisonItem
                .setOperationTypes(
                    Collections.singletonList(dependencyContract.getOperationType()));
            dependencyItemMap.put(dependencyId, dependencyComparisonItem);
          }
        }
        tempReplayComparisonItem.setDependencyComparisonItems(
            new ArrayList<>(dependencyItemMap.values()));
        replayConfigurationMap.put(operationId, tempReplayComparisonItem);
      }
    }
  }

  protected void mergeGlobalComparisonConfig(
      Map<String, ReplayCompareConfig.ReplayComparisonItem> replayConfigurationMap,
      Map<String, ApplicationOperationConfiguration> operationInfoMap,
      Map<String, Map<String, AppContractDto>> appContractDtoMap) {

    if (!replayConfigurationMap.containsKey(null)) {
      return;
    }

    Set<String> operationIdList = operationInfoMap.keySet();

    ReplayCompareConfig.ReplayComparisonItem globalConfig = replayConfigurationMap.get(null);
    Set<List<String>> globalExclusionList = globalConfig.getExclusionList();
    Set<List<String>> globalInclusionList = globalConfig.getInclusionList();
    List<String> globalIgnoreCategoryList = globalConfig.getIgnoreCategoryTypes();

    for (String operationId : operationIdList) {
      ReplayCompareConfig.ReplayComparisonItem tempReplayConfig =
          replayConfigurationMap.getOrDefault(operationId,
              new ReplayCompareConfig.ReplayComparisonItem());

      tempReplayConfig.setOperationId(operationId);
      ApplicationOperationConfiguration operationConfiguration = operationInfoMap.get(operationId);
      tempReplayConfig.setOperationTypes(
          new ArrayList<>(operationConfiguration.getOperationTypes()));
      tempReplayConfig.setOperationName(operationConfiguration.getOperationName());

      Map<String, ReplayCompareConfig.DependencyComparisonItem> dependencyConfigMap =
          Optional.ofNullable(tempReplayConfig.getDependencyComparisonItems())
              .orElse(Collections.emptyList())
              .stream().collect(
                  Collectors.toMap(ReplayCompareConfig.DependencyComparisonItem::getDependencyId,
                      Function.identity()));

      if (globalExclusionList != null) {

        Set<List<String>> exclusionList =
            tempReplayConfig.getExclusionList() == null ? new HashSet<>()
                : tempReplayConfig.getExclusionList();
        exclusionList.addAll(globalExclusionList);
        tempReplayConfig.setExclusionList(exclusionList);

        Collection<AppContractDto> appContractDtoList =
            appContractDtoMap.getOrDefault(operationId, Collections.emptyMap()).values();
        for (AppContractDto appContractDto : appContractDtoList) {
          String dependencyId = appContractDto.getId();
          if (dependencyConfigMap.containsKey(dependencyId)) {
            ReplayCompareConfig.DependencyComparisonItem dependencyComparisonItem =
                dependencyConfigMap.get(dependencyId);
            Set<List<String>> previousDependencyExclusion = dependencyComparisonItem.getExclusionList();
            Set<List<String>> dependencyExclusionList = previousDependencyExclusion == null
                ? new HashSet<>() : dependencyComparisonItem.getExclusionList();
            dependencyExclusionList.addAll(globalExclusionList);
            dependencyComparisonItem.setExclusionList(dependencyExclusionList);
          } else {
            ReplayCompareConfig.DependencyComparisonItem dependencyComparisonItem =
                new ReplayCompareConfig.DependencyComparisonItem();
            dependencyComparisonItem.setDependencyId(dependencyId);
            dependencyComparisonItem.setOperationName(appContractDto.getOperationName());
            dependencyComparisonItem
                .setOperationTypes(Collections.singletonList(appContractDto.getOperationType()));
            dependencyComparisonItem.setExclusionList(globalExclusionList);
            dependencyConfigMap.put(dependencyId, dependencyComparisonItem);
          }
        }
      }

      if (globalInclusionList != null) {
        Set<List<String>> inclusionList =
            tempReplayConfig.getInclusionList() == null ? new HashSet<>()
                : tempReplayConfig.getInclusionList();
        inclusionList.addAll(globalInclusionList);
        tempReplayConfig.setInclusionList(inclusionList);

        Collection<AppContractDto> appContractDtoList =
            appContractDtoMap.getOrDefault(operationId, Collections.emptyMap()).values();
        for (AppContractDto appContractDto : appContractDtoList) {
          String dependencyId = appContractDto.getId();
          if (dependencyConfigMap.containsKey(dependencyId)) {
            ReplayCompareConfig.DependencyComparisonItem dependencyComparisonItem =
                dependencyConfigMap.get(dependencyId);
            Set<List<String>> previousDependencyInclusion = dependencyComparisonItem.getInclusionList();
            Set<List<String>> dependencyInclusionList = previousDependencyInclusion == null
                ? new HashSet<>() : dependencyComparisonItem.getInclusionList();
            dependencyInclusionList.addAll(globalInclusionList);
            dependencyComparisonItem.setInclusionList(dependencyInclusionList);
          } else {
            ReplayCompareConfig.DependencyComparisonItem dependencyComparisonItem =
                new ReplayCompareConfig.DependencyComparisonItem();
            dependencyComparisonItem.setDependencyId(dependencyId);
            dependencyComparisonItem.setOperationName(appContractDto.getOperationName());
            dependencyComparisonItem
                .setOperationTypes(Collections.singletonList(appContractDto.getOperationType()));
            dependencyComparisonItem.setInclusionList(globalInclusionList);
            dependencyConfigMap.put(dependencyId, dependencyComparisonItem);
          }
        }
      }

      if (globalIgnoreCategoryList != null) {
        List<String> ignoreCategoryList = tempReplayConfig.getIgnoreCategoryTypes() == null
            ? new ArrayList<>() : tempReplayConfig.getIgnoreCategoryTypes();
        ignoreCategoryList.addAll(globalIgnoreCategoryList);
        tempReplayConfig.setIgnoreCategoryTypes(ignoreCategoryList);

        Collection<AppContractDto> appContractDtoList =
            appContractDtoMap.getOrDefault(operationId, Collections.emptyMap()).values();
        for (AppContractDto appContractDto : appContractDtoList) {
          String dependencyId = appContractDto.getId();
          if (dependencyConfigMap.containsKey(dependencyId)) {
            ReplayCompareConfig.DependencyComparisonItem dependencyComparisonItem =
                dependencyConfigMap.get(dependencyId);
            List<String> previousDependencyExclusionCategories = dependencyComparisonItem.getIgnoreCategoryTypes();
            List<String> dependencyExclusionCategories =
                previousDependencyExclusionCategories == null
                    ? new ArrayList<>() : dependencyComparisonItem.getIgnoreCategoryTypes();
            dependencyExclusionCategories.addAll(globalIgnoreCategoryList);
            dependencyComparisonItem.setIgnoreCategoryTypes(dependencyExclusionCategories);
          } else {
            ReplayCompareConfig.DependencyComparisonItem dependencyComparisonItem =
                new ReplayCompareConfig.DependencyComparisonItem();
            dependencyComparisonItem.setDependencyId(dependencyId);
            dependencyComparisonItem.setOperationName(appContractDto.getOperationName());
            dependencyComparisonItem
                .setOperationTypes(Collections.singletonList(appContractDto.getOperationType()));
            dependencyComparisonItem.setIgnoreCategoryTypes(globalIgnoreCategoryList);
            dependencyConfigMap.put(dependencyId, dependencyComparisonItem);
          }
        }
      }

      tempReplayConfig.setDependencyComparisonItems(new ArrayList<>(dependencyConfigMap.values()));
      replayConfigurationMap.put(operationId, tempReplayConfig);

    }
  }

  /**
   * Set default values for missing dependency
   *
   * @param replayCompareConfig
   */
  protected void setDefaultWhenMissingDependency(ReplayComparisonItem globalComparisonItem,
      ReplayCompareConfig replayCompareConfig) {
    if (globalComparisonItem == null) {
      return;
    }
    DependencyComparisonItem defaultComparisonItem = new DependencyComparisonItem();
    BeanUtils.copyProperties(globalComparisonItem, defaultComparisonItem);
    List<ReplayComparisonItem> replayComparisonItems = Optional.ofNullable(
        replayCompareConfig.getReplayComparisonItems()).orElse(Collections.emptyList());
    for (ReplayComparisonItem item : replayComparisonItems) {
      item.setDefaultDependencyComparisonItem(defaultComparisonItem);
    }
  }

  @Data
  public static class AppOperationAndDependencyInfo {

    // key: operationId, value: operationInfo
    private Map<String, ApplicationOperationConfiguration> operationMap;
    // key: operationId, value: <dependencyId, AppContractDto>
    private Map<String, Map<String, AppContractDto>> appContractDtoMap;
  }

}
