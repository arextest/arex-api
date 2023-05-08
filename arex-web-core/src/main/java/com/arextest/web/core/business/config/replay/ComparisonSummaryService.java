package com.arextest.web.core.business.config.replay;

import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.contract.contracts.config.application.ApplicationServiceConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by rchen9 on 2023/2/7.
 */
@Component
public class ComparisonSummaryService {

    @Resource
    ComparisonExclusionsConfigurableHandler comparisonExclusionsConfigurableHandler;
    @Resource
    ComparisonInclusionsConfigurableHandler comparisonInclusionsConfigurableHandler;
    @Resource
    ComparisonReferenceConfigurableHandler comparisonReferenceConfigurableHandler;
    @Resource
    ComparisonListSortConfigurableHandler comparisonListSortConfigurableHandler;
    @Resource
    ConfigurableHandler<ApplicationServiceConfiguration> applicationServiceConfigurationConfigurableHandler;


    public ComparisonSummaryConfiguration getComparisonDetailsSummary(String interfaceId) {
        ComparisonSummaryConfiguration comparisonSummaryConfiguration =
                new ComparisonSummaryConfiguration();
        getComparisonExclusionsConfiguration(interfaceId, comparisonSummaryConfiguration);
        getComparisonInclusionsConfiguration(interfaceId, comparisonSummaryConfiguration);
        getComparisonListSortConfiguration(interfaceId, comparisonSummaryConfiguration);
        getComparisonReferenceConfiguration(interfaceId, comparisonSummaryConfiguration);
        return comparisonSummaryConfiguration;
    }

    public ReplayComparisonConfig queryConfig(String appId) {
        ReplayComparisonConfig replayComparisonConfig = new ReplayComparisonConfig();
        replayComparisonConfig.setReplayComparisonItems(getReplaySummaryConfig(appId));
        return replayComparisonConfig;
    }

    private void getComparisonExclusionsConfiguration(String interfaceId,
                                                      ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
        Set<List<String>> exclusionSet = new HashSet<>();
        List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurationList =
                comparisonExclusionsConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonExclusionsConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            exclusionSet.add(item.getExclusions());
        });
        comparisonSummaryConfiguration.setExclusionList(exclusionSet);
    }

    private void getComparisonInclusionsConfiguration(String interfaceId,
                                                      ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
        Set<List<String>> inclusionSet = new HashSet<>();
        List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurationList =
                comparisonInclusionsConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonInclusionsConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            inclusionSet.add(item.getInclusions());
        });
        comparisonSummaryConfiguration.setInclusionList(inclusionSet);
    }

    private void getComparisonListSortConfiguration(String interfaceId,
                                                    ComparisonSummaryConfiguration comparisonSummaryConfiguration) {
        Map<List<String>, List<List<String>>> listSortMap = new HashMap<>();
        List<ComparisonListSortConfiguration> comparisonListSortConfigurationList =
                comparisonListSortConfigurableHandler.queryByInterfaceId(interfaceId);
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
                comparisonReferenceConfigurableHandler.queryByInterfaceId(interfaceId);
        Optional.ofNullable(comparisonReferenceConfigurationList).orElse(Collections.emptyList()).forEach(item -> {
            if (CollectionUtils.isNotEmpty(item.getFkPath())) {
                referenceMap.put(item.getFkPath(), item.getPkPath());
            }
        });
        comparisonSummaryConfiguration.setReferenceMap(referenceMap);
    }

    private List<ReplayComparisonConfig.ReplayComparisonItem> getReplaySummaryConfig(String appId) {
        Map<String, ReplayComparisonConfig.ReplayComparisonItem> replayConfigurationMap = new HashMap<>();
        buildExclusions(replayConfigurationMap, comparisonExclusionsConfigurableHandler.useResultAsList(appId));
        buildInclusions(replayConfigurationMap, comparisonInclusionsConfigurableHandler.useResultAsList(appId));
        buildListSort(replayConfigurationMap, comparisonListSortConfigurableHandler.useResultAsList(appId));
        buildReference(replayConfigurationMap, comparisonReferenceConfigurableHandler.useResultAsList(appId));
        mergeGlobalComparisonConfig(replayConfigurationMap, appId);
        return new ArrayList<>(replayConfigurationMap.values());
    }

    private void mergeGlobalComparisonConfig(Map<String, ReplayComparisonConfig.ReplayComparisonItem> replayConfigurationMap,
                                             String appId) {
        if (replayConfigurationMap.containsKey(null)) {

            ReplayComparisonConfig.ReplayComparisonItem globalConfig = replayConfigurationMap.get(null);
            Set<List<String>> globalExclusionList = globalConfig.getExclusionList();
            Set<List<String>> globalInclusionList = globalConfig.getInclusionList();

            List<String> operationIdList = getOperationIdList(appId);
            for (String operationId : operationIdList) {
                ReplayComparisonConfig.ReplayComparisonItem tempReplayConfig = replayConfigurationMap.getOrDefault(
                        operationId, new ReplayComparisonConfig.ReplayComparisonItem());
                tempReplayConfig.setOperationId(operationId);

                if (globalExclusionList != null) {
                    Set<List<String>> exclusionList = tempReplayConfig.getExclusionList() == null ?
                            new HashSet<>() : tempReplayConfig.getExclusionList();
                    exclusionList.addAll(globalExclusionList);
                    tempReplayConfig.setExclusionList(exclusionList);
                }

                if (globalInclusionList != null) {
                    Set<List<String>> inclusionList = tempReplayConfig.getInclusionList() == null ?
                            new HashSet<>() : tempReplayConfig.getInclusionList();
                    inclusionList.addAll(globalInclusionList);
                    tempReplayConfig.setInclusionList(inclusionList);
                }
                replayConfigurationMap.put(operationId, tempReplayConfig);
            }
            replayConfigurationMap.remove(null);
        }
    }

    private List<String> getOperationIdList(String appId) {
        List<String> operationIdList = new ArrayList<>();

        List<ApplicationServiceConfiguration> applicationServiceConfigurations =
                applicationServiceConfigurationConfigurableHandler.useResultAsList(appId);

        Optional.ofNullable(applicationServiceConfigurations)
                .orElse(Collections.emptyList())
                .forEach(item -> {
                    List<ApplicationOperationConfiguration> operationList = item.getOperationList();
                    if (CollectionUtils.isNotEmpty(operationList)) {
                        for (ApplicationOperationConfiguration applicationOperationConfiguration : operationList) {
                            operationIdList.add(applicationOperationConfiguration.getId());
                        }
                    }
                });
        return operationIdList;
    }

    private void buildExclusions(Map<String, ReplayComparisonConfig.ReplayComparisonItem> replayConfigurationMap,
                                 List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurations) {
        if (CollectionUtils.isNotEmpty(comparisonExclusionsConfigurations)) {
            for (ComparisonExclusionsConfiguration compareExclusionsConfig : comparisonExclusionsConfigurations) {

                String operationId = compareExclusionsConfig.getOperationId();
                List<String> exclusions = compareExclusionsConfig.getExclusions();

                if (CollectionUtils.isNotEmpty(exclusions)) {
                    ReplayComparisonConfig.ReplayComparisonItem tempReplayComparisonItem =
                            replayConfigurationMap.getOrDefault(operationId, new ReplayComparisonConfig.ReplayComparisonItem());
                    tempReplayComparisonItem.setOperationId(operationId);

                    Set<List<String>> tempExclusionList = tempReplayComparisonItem.getExclusionList() ==
                            null ? new HashSet<>() : tempReplayComparisonItem.getExclusionList();
                    tempExclusionList.add(exclusions);
                    tempReplayComparisonItem.setExclusionList(tempExclusionList);
                    replayConfigurationMap.put(operationId, tempReplayComparisonItem);
                }
            }
        }
    }

    private void buildInclusions(Map<String, ReplayComparisonConfig.ReplayComparisonItem> replayConfigurationMap,
                                 List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurations) {
        if (CollectionUtils.isNotEmpty(comparisonInclusionsConfigurations)) {
            for (ComparisonInclusionsConfiguration comparisonInclusionsConfiguration : comparisonInclusionsConfigurations) {

                String operationId = comparisonInclusionsConfiguration.getOperationId();
                List<String> inclusions = comparisonInclusionsConfiguration.getInclusions();

                if (CollectionUtils.isNotEmpty(inclusions)) {
                    ReplayComparisonConfig.ReplayComparisonItem tempReplayComparisonItem =
                            replayConfigurationMap.getOrDefault(operationId, new ReplayComparisonConfig.ReplayComparisonItem());
                    tempReplayComparisonItem.setOperationId(operationId);

                    Set<List<String>> tempInclusionList = tempReplayComparisonItem.getInclusionList() ==
                            null ? new HashSet<>() : tempReplayComparisonItem.getInclusionList();
                    tempInclusionList.add(inclusions);
                    tempReplayComparisonItem.setInclusionList(tempInclusionList);
                    replayConfigurationMap.put(operationId, tempReplayComparisonItem);
                }
            }
        }
    }

    private void buildListSort(Map<String, ReplayComparisonConfig.ReplayComparisonItem> replayConfigurationMap,
                               List<ComparisonListSortConfiguration> comparisonListSortConfigurations) {
        if (CollectionUtils.isNotEmpty(comparisonListSortConfigurations)) {
            for (ComparisonListSortConfiguration compareListSortConfig : comparisonListSortConfigurations) {

                String operationId = compareListSortConfig.getOperationId();
                List<String> listPath = compareListSortConfig.getListPath();
                List<List<String>> keys = compareListSortConfig.getKeys();

                if (CollectionUtils.isNotEmpty(listPath) && CollectionUtils.isNotEmpty(keys)) {
                    ReplayComparisonConfig.ReplayComparisonItem tempReplayComparisonItem =
                            replayConfigurationMap.getOrDefault(operationId, new ReplayComparisonConfig.ReplayComparisonItem());
                    tempReplayComparisonItem.setOperationId(operationId);

                    Map<List<String>, List<List<String>>> tempListSortMap = tempReplayComparisonItem.getListSortMap() ==
                            null ? new HashMap<>() : tempReplayComparisonItem.getListSortMap();
                    tempListSortMap.put(listPath, keys);
                    tempReplayComparisonItem.setListSortMap(tempListSortMap);
                    replayConfigurationMap.put(operationId, tempReplayComparisonItem);
                }
            }
        }
    }

    private void buildReference(Map<String, ReplayComparisonConfig.ReplayComparisonItem> replayConfigurationMap,
                                List<ComparisonReferenceConfiguration> comparisonReferenceConfigurations) {
        if (CollectionUtils.isNotEmpty(comparisonReferenceConfigurations)) {
            for (ComparisonReferenceConfiguration comparisonReferenceConfiguration : comparisonReferenceConfigurations) {

                String operationId = comparisonReferenceConfiguration.getOperationId();
                List<String> fkPath = comparisonReferenceConfiguration.getFkPath();
                List<String> pkPath = comparisonReferenceConfiguration.getPkPath();

                if (CollectionUtils.isNotEmpty(fkPath) && CollectionUtils.isNotEmpty(pkPath)) {
                    ReplayComparisonConfig.ReplayComparisonItem tempReplayComparisonItem =
                            replayConfigurationMap.getOrDefault(operationId, new ReplayComparisonConfig.ReplayComparisonItem());
                    tempReplayComparisonItem.setOperationId(operationId);

                    Map<List<String>, List<String>> tempReferenceMap = tempReplayComparisonItem.getReferenceMap() ==
                            null ? new HashMap<>() : tempReplayComparisonItem.getReferenceMap();
                    tempReferenceMap.put(fkPath, pkPath);
                    tempReplayComparisonItem.setReferenceMap(tempReferenceMap);
                    replayConfigurationMap.put(operationId, tempReplayComparisonItem);
                }
            }
        }
    }

}
