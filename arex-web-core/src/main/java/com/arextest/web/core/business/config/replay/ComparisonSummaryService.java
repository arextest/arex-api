package com.arextest.web.core.business.config.replay;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.contract.contracts.config.application.ApplicationServiceConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.*;
import com.arextest.web.model.dao.mongodb.AppContractCollection;
import com.arextest.web.model.dto.AppContractDto;
import com.arextest.web.model.enums.ContractTypeEnum;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Component
public class ComparisonSummaryService {

    @Resource
    ComparisonExclusionsConfigurableHandler exclusionsConfigurableHandler;
    @Resource
    ComparisonInclusionsConfigurableHandler inclusionsConfigurableHandler;
    @Resource
    ComparisonReferenceConfigurableHandler referenceConfigurableHandler;
    @Resource
    ComparisonListSortConfigurableHandler listSortConfigurableHandler;
    @Resource
    ConfigurableHandler<ApplicationServiceConfiguration> applicationServiceConfigurationConfigurableHandler;
    @Resource
    AppContractRepository appContractRepository;

    public ComparisonSummaryConfiguration getComparisonDetailsSummary(String interfaceId) {
        ComparisonSummaryConfiguration comparisonSummaryConfiguration = new ComparisonSummaryConfiguration();
        getComparisonExclusionsConfiguration(interfaceId, comparisonSummaryConfiguration);
        getComparisonInclusionsConfiguration(interfaceId, comparisonSummaryConfiguration);
        getComparisonListSortConfiguration(interfaceId, comparisonSummaryConfiguration);
        getComparisonReferenceConfiguration(interfaceId, comparisonSummaryConfiguration);
        return comparisonSummaryConfiguration;
    }

    public ReplayCompareConfig getReplayComparisonConfig(String appId) {
        ReplayCompareConfig replayCompareConfig = new ReplayCompareConfig();
        replayCompareConfig.setReplayComparisonItems(getReplayComparisonItems(appId));
        return replayCompareConfig;
    }

    private void getComparisonExclusionsConfiguration(String interfaceId,
        ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
        Set<List<String>> exclusionSet = new HashSet<>();
        List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurationList =
            exclusionsConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonExclusionsConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            exclusionSet.add(item.getExclusions());
        });
        comparisonSummaryConfiguration.setExclusionList(exclusionSet);
    }

    private void getComparisonInclusionsConfiguration(String interfaceId,
        ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
        Set<List<String>> inclusionSet = new HashSet<>();
        List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurationList =
            inclusionsConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonInclusionsConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            inclusionSet.add(item.getInclusions());
        });
        comparisonSummaryConfiguration.setInclusionList(inclusionSet);
    }

    private void getComparisonListSortConfiguration(String interfaceId,
        ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
        Map<List<String>, List<List<String>>> listSortMap = new HashMap<>();
        List<ComparisonListSortConfiguration> comparisonListSortConfigurationList =
            listSortConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonListSortConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getListPath())) {
                listSortMap.put(item.getListPath(), item.getKeys());
            }
        });
        comparisonSummaryConfiguration.setListSortMap(listSortMap);
    }

    private void getComparisonReferenceConfiguration(String interfaceId,
        ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
        Map<List<String>, List<String>> referenceMap = new HashMap<>();
        List<ComparisonReferenceConfiguration> comparisonReferenceConfigurationList =
            referenceConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonReferenceConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getFkPath())) {
                referenceMap.put(item.getFkPath(), item.getPkPath());
            }
        });
        comparisonSummaryConfiguration.setReferenceMap(referenceMap);
    }

    private List<ReplayCompareConfig.ReplayComparisonItem> getReplayComparisonItems(String appId) {
        Map<String, ReplayCompareConfig.ReplayComparisonItem> replayConfigurationMap = new HashMap<>();

        List<String> operationIdList = getOperationIdList(appId);

        // appContractDtoList filter the operation and group by operationId
        List<AppContractDto> appContractDtos = appContractRepository.queryAppContractListByOpId(operationIdList,
            Collections.singletonList(AppContractCollection.Fields.contract));
        // Map<String,
        // List<AppContractDto>> appContractDtoMap = appContractDtos.stream()
        // .filter(item -> Objects.equals(item.getContractType(), ContractTypeEnum.DEPENDENCY.getCode()))
        // .collect(Collectors.groupingBy(AppContractDto::getOperationId));

        Map<String,
            Map<String, AppContractDto>> appContractDtoMap = appContractDtos.stream()
                .filter(item -> Objects.equals(item.getContractType(), ContractTypeEnum.DEPENDENCY.getCode()))
                .collect(Collectors.groupingBy(AppContractDto::getOperationId,
                    Collectors.toMap(AppContractDto::getId, Function.identity())));

        buildComparisonConfig(replayConfigurationMap, exclusionsConfigurableHandler.useResultAsList(appId),
            (configurations, summaryConfiguration) -> {
                Set<List<String>> operationExclusion = configurations.stream()
                    .map(ComparisonExclusionsConfiguration::getExclusions).collect(Collectors.toSet());
                summaryConfiguration.setExclusionList(operationExclusion);
            }, appContractDtoMap);

        buildComparisonConfig(replayConfigurationMap, inclusionsConfigurableHandler.useResultAsList(appId),
            (configurations, summaryConfiguration) -> {
                Set<List<String>> operationInclusion = configurations.stream()
                    .map(ComparisonInclusionsConfiguration::getInclusions).collect(Collectors.toSet());
                summaryConfiguration.setInclusionList(operationInclusion);
            }, appContractDtoMap);

        buildComparisonConfig(replayConfigurationMap, listSortConfigurableHandler.useResultAsList(appId),
            (configurations, summaryConfiguration) -> {
                Map<List<String>,
                    List<List<String>>> operationListSortMap = configurations.stream()
                        .filter(item -> CollectionUtils.isNotEmpty(item.getListPath())
                            && CollectionUtils.isNotEmpty(item.getKeys()))
                        .collect(Collectors.toMap(ComparisonListSortConfiguration::getListPath,
                            ComparisonListSortConfiguration::getKeys));
                summaryConfiguration.setListSortMap(operationListSortMap);
            }, appContractDtoMap);

        buildComparisonConfig(replayConfigurationMap, referenceConfigurableHandler.useResultAsList(appId),
            (configurations, summaryConfiguration) -> {
                Map<List<String>,
                    List<String>> operationReferenceMap = configurations.stream()
                        .filter(item -> CollectionUtils.isNotEmpty(item.getFkPath())
                            && CollectionUtils.isNotEmpty(item.getPkPath()))
                        .collect(Collectors.toMap(ComparisonReferenceConfiguration::getFkPath,
                            ComparisonReferenceConfiguration::getPkPath));
                summaryConfiguration.setReferenceMap(operationReferenceMap);
            }, appContractDtoMap);

        mergeGlobalComparisonConfig(replayConfigurationMap, operationIdList, appContractDtoMap);
        return new ArrayList<>(replayConfigurationMap.values());
    }

    private void mergeGlobalComparisonConfig(
        Map<String, ReplayCompareConfig.ReplayComparisonItem> replayConfigurationMap, List<String> operationIdList,
        Map<String, Map<String, AppContractDto>> appContractDtoMap) {

        if (replayConfigurationMap.containsKey(null)) {

            ReplayCompareConfig.ReplayComparisonItem globalConfig = replayConfigurationMap.get(null);
            Set<List<String>> globalExclusionList = globalConfig.getExclusionList();
            Set<List<String>> globalInclusionList = globalConfig.getInclusionList();

            for (String operationId : operationIdList) {
                ReplayCompareConfig.ReplayComparisonItem tempReplayConfig =
                    replayConfigurationMap.getOrDefault(operationId, new ReplayCompareConfig.ReplayComparisonItem());
                tempReplayConfig.setOperationId(operationId);

                Map<String, ReplayCompareConfig.DependencyComparisonItem> dependencyConfigMap =
                    Optional.ofNullable(tempReplayConfig.getDependencyComparisonItems()).orElse(Collections.emptyList())
                        .stream().collect(Collectors
                            .toMap(ReplayCompareConfig.DependencyComparisonItem::getDependencyId, Function.identity()));

                if (globalExclusionList != null) {

                    Set<List<String>> exclusionList = tempReplayConfig.getExclusionList() == null ? new HashSet<>()
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
                            dependencyComparisonItem.setDependencyName(appContractDto.getOperationName());
                            dependencyComparisonItem.setDependencyType(appContractDto.getOperationType());
                            dependencyComparisonItem.setExclusionList(globalExclusionList);
                            dependencyConfigMap.put(dependencyId, dependencyComparisonItem);
                        }
                    }
                }

                if (globalInclusionList != null) {
                    Set<List<String>> inclusionList = tempReplayConfig.getInclusionList() == null ? new HashSet<>()
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
                            dependencyComparisonItem.setDependencyName(appContractDto.getOperationName());
                            dependencyComparisonItem.setDependencyType(appContractDto.getOperationType());
                            dependencyComparisonItem.setInclusionList(globalInclusionList);
                            dependencyConfigMap.put(dependencyId, dependencyComparisonItem);
                        }
                    }
                }

                tempReplayConfig.setDependencyComparisonItems(new ArrayList<>(dependencyConfigMap.values()));
                // (List<ReplayCompareConfig.DependencyComparisonItem>)dependencyConfigMap.values());
                replayConfigurationMap.put(operationId, tempReplayConfig);
            }
            replayConfigurationMap.remove(null);
        }
    }

    private List<String> getOperationIdList(String appId) {
        List<String> operationIdList = new ArrayList<>();

        List<ApplicationServiceConfiguration> applicationServiceConfigurations =
            applicationServiceConfigurationConfigurableHandler.useResultAsList(appId);

        Optional.ofNullable(applicationServiceConfigurations).orElse(Collections.emptyList()).forEach(item -> {
            List<ApplicationOperationConfiguration> operationList = item.getOperationList();
            if (CollectionUtils.isNotEmpty(operationList)) {
                for (ApplicationOperationConfiguration applicationOperationConfiguration : operationList) {
                    operationIdList.add(applicationOperationConfiguration.getId());
                }
            }
        });
        return operationIdList;
    }

    private <T extends AbstractComparisonDetailsConfiguration> void buildComparisonConfig(
        Map<String, ReplayCompareConfig.ReplayComparisonItem> replayConfigurationMap, List<T> configurations,
        BiConsumer<List<T>, ComparisonSummaryConfiguration> assignFunction,
        Map<String, Map<String, AppContractDto>> appContractDtoMap) {

        if (CollectionUtils.isNotEmpty(configurations)) {
            // comparisonExclusionsConfigurations group by operationId
            Map<String, List<T>> configurationsMap = new HashMap<>();
            for (T config : configurations) {
                String operationId = config.getOperationId();
                List<T> list = configurationsMap.getOrDefault(operationId, new ArrayList<>());
                list.add(config);
                configurationsMap.put(operationId, list);
            }

            for (Map.Entry<String, List<T>> entry : configurationsMap.entrySet()) {

                String operationId = entry.getKey();
                List<T> configurationList = entry.getValue();

                ReplayCompareConfig.ReplayComparisonItem tempReplayComparisonItem =
                    replayConfigurationMap.getOrDefault(operationId, new ReplayCompareConfig.ReplayComparisonItem());
                tempReplayComparisonItem.setOperationId(operationId);

                // configurationList group by dependencyId if key is null, it means the configuration of
                // operation. if key is not null, it means the configuration of dependency.
                Map<String, List<T>> dependencyConfigurationsMap = new HashMap<>();
                for (T config : configurationList) {
                    String dependencyId = config.getDependencyId();
                    List<T> list = dependencyConfigurationsMap.getOrDefault(dependencyId, new ArrayList<>());
                    list.add(config);
                    dependencyConfigurationsMap.put(dependencyId, list);
                }

                // build the configuration of operation
                List<T> operationConfig = dependencyConfigurationsMap.getOrDefault(null, Collections.emptyList());
                assignFunction.accept(operationConfig, tempReplayComparisonItem);

                // build the configuration of dependency
                Map<String, ReplayCompareConfig.DependencyComparisonItem> dependencyItemMap =
                    Optional.ofNullable(tempReplayComparisonItem.getDependencyComparisonItems())
                        .orElse(Collections.emptyList()).stream().collect(Collectors
                            .toMap(ReplayCompareConfig.DependencyComparisonItem::getDependencyId, Function.identity()));

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
                        dependencyComparisonItem.setDependencyName(dependencyContract.getOperationName());
                        dependencyComparisonItem.setDependencyType(dependencyContract.getOperationType());
                        dependencyItemMap.put(dependencyId, dependencyComparisonItem);
                    }
                }
                tempReplayComparisonItem.setDependencyComparisonItems(new ArrayList<>(dependencyItemMap.values()));
                replayConfigurationMap.put(operationId, tempReplayComparisonItem);
            }
        }
    }
}
