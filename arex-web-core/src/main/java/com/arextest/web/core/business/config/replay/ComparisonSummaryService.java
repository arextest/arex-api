package com.arextest.web.core.business.config.replay;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
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
        Map<String, ReplayCompareConfig.ReplayComparisonItem> replayComparisonItemMap = getReplayComparisonItems(appId);
        // build global config
        ReplayCompareConfig.GlobalComparisonItem globalComparisonItem = new ReplayCompareConfig.GlobalComparisonItem();
        ReplayCompareConfig.ReplayComparisonItem replayComparisonItem = replayComparisonItemMap.get(null);
        if (replayComparisonItem != null) {
            BeanUtils.copyProperties(replayComparisonItem, globalComparisonItem);
            replayCompareConfig.setGlobalComparisonItem(globalComparisonItem);
        }
        replayComparisonItemMap.remove(null);
        // build operation config
        replayCompareConfig.setReplayComparisonItems(new ArrayList<>(replayComparisonItemMap.values()));
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

    private Map<String, ReplayCompareConfig.ReplayComparisonItem> getReplayComparisonItems(String appId) {
        Map<String, ReplayCompareConfig.ReplayComparisonItem> replayConfigurationMap = new HashMap<>();

        Map<String, ApplicationOperationConfiguration> operationInfoMap = getOperationInfos(appId);

        List<String> operationIdList = new ArrayList<>(operationInfoMap.keySet());

        // appContractDtoList filter the operation and group by operationId
        List<AppContractDto> appContractDtos = appContractRepository.queryAppContractListByOpIds(operationIdList,
            Collections.singletonList(AppContractCollection.Fields.contract));

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
            }, appContractDtoMap, operationInfoMap);

        buildComparisonConfig(replayConfigurationMap, inclusionsConfigurableHandler.useResultAsList(appId),
            (configurations, summaryConfiguration) -> {
                Set<List<String>> operationInclusion = configurations.stream()
                    .map(ComparisonInclusionsConfiguration::getInclusions).collect(Collectors.toSet());
                summaryConfiguration.setInclusionList(operationInclusion);
            }, appContractDtoMap, operationInfoMap);

        buildComparisonConfig(replayConfigurationMap, listSortConfigurableHandler.useResultAsList(appId),
            (configurations, summaryConfiguration) -> {
                Map<List<String>,
                    List<List<String>>> operationListSortMap = configurations.stream()
                        .filter(item -> CollectionUtils.isNotEmpty(item.getListPath())
                            && CollectionUtils.isNotEmpty(item.getKeys()))
                        .collect(Collectors.toMap(ComparisonListSortConfiguration::getListPath,
                            ComparisonListSortConfiguration::getKeys));
                summaryConfiguration.setListSortMap(operationListSortMap);
            }, appContractDtoMap, operationInfoMap);

        buildComparisonConfig(replayConfigurationMap, referenceConfigurableHandler.useResultAsList(appId),
            (configurations, summaryConfiguration) -> {
                Map<List<String>,
                    List<String>> operationReferenceMap = configurations.stream()
                        .filter(item -> CollectionUtils.isNotEmpty(item.getFkPath())
                            && CollectionUtils.isNotEmpty(item.getPkPath()))
                        .collect(Collectors.toMap(ComparisonReferenceConfiguration::getFkPath,
                            ComparisonReferenceConfiguration::getPkPath));
                summaryConfiguration.setReferenceMap(operationReferenceMap);
            }, appContractDtoMap, operationInfoMap);
        return replayConfigurationMap;
    }

    /**
     *
     * get the info of operation key: operationId value: operationType
     *
     * @param appId
     * @return
     */
    private Map<String, ApplicationOperationConfiguration> getOperationInfos(String appId) {
        Map<String, ApplicationOperationConfiguration> operationInfo = new HashMap<>();

        List<ApplicationServiceConfiguration> applicationServiceConfigurations =
            applicationServiceConfigurationConfigurableHandler.useResultAsList(appId);

        Optional.ofNullable(applicationServiceConfigurations).orElse(Collections.emptyList()).forEach(item -> {
            List<ApplicationOperationConfiguration> operationList = item.getOperationList();
            if (CollectionUtils.isNotEmpty(operationList)) {
                for (ApplicationOperationConfiguration operationConfiguration : operationList) {
                    operationInfo.put(operationConfiguration.getId(), operationConfiguration);
                }
            }
        });
        return operationInfo;
    }

    private <T extends AbstractComparisonDetailsConfiguration> void buildComparisonConfig(
        Map<String, ReplayCompareConfig.ReplayComparisonItem> replayConfigurationMap, List<T> configurations,
        BiConsumer<List<T>, ComparisonSummaryConfiguration> assignFunction,
        Map<String, Map<String, AppContractDto>> appContractDtoMap,
        Map<String, ApplicationOperationConfiguration> operationMap) {

        if (CollectionUtils.isNotEmpty(configurations)) {
            // comparisonExclusionsConfigurations group by operationId
            Map<String, List<T>> configurationsMap = new HashMap<>();
            for (T config : configurations) {
                configurationsMap.computeIfAbsent(config.getOperationId(), k -> new ArrayList<>()).add(config);
            }

            for (Map.Entry<String, List<T>> entry : configurationsMap.entrySet()) {

                String operationId = entry.getKey();
                List<T> configurationList = entry.getValue();

                ReplayCompareConfig.ReplayComparisonItem tempReplayComparisonItem =
                    replayConfigurationMap.getOrDefault(operationId, new ReplayCompareConfig.ReplayComparisonItem());
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
                    dependencyConfigurationsMap.computeIfAbsent(config.getDependencyId(), k -> new ArrayList<>())
                        .add(config);
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
                        dependencyComparisonItem.setOperationName(dependencyContract.getOperationName());
                        dependencyComparisonItem
                            .setOperationTypes(Collections.singletonList(dependencyContract.getOperationType()));
                        dependencyItemMap.put(dependencyId, dependencyComparisonItem);
                    }
                }
                tempReplayComparisonItem.setDependencyComparisonItems(new ArrayList<>(dependencyItemMap.values()));
                replayConfigurationMap.put(operationId, tempReplayComparisonItem);
            }
        }
    }

}
