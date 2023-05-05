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


    public ReplayConfiguration queryConfig(String appId) {
        ReplayConfiguration replayConfiguration = new ReplayConfiguration();
        replayConfiguration.setReplayComparisonConfigs(getReplayConfigurations(appId));
        return replayConfiguration;
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

    private List<ReplayConfiguration.ReplayComparisonConfig> getReplayConfigurations(String appId) {
        Map<String, ReplayConfiguration.ReplayComparisonConfig> replayConfigurationMap = new HashMap<>();
        buildExclusions(replayConfigurationMap, comparisonExclusionsConfigurableHandler.useResultAsList(appId));
        buildInclusions(replayConfigurationMap, comparisonInclusionsConfigurableHandler.useResultAsList(appId));
        buildListSort(replayConfigurationMap, comparisonListSortConfigurableHandler.useResultAsList(appId));
        buildReference(replayConfigurationMap, comparisonReferenceConfigurableHandler.useResultAsList(appId));
        mergeGlobalComparisonConfig(replayConfigurationMap, appId);
        return new ArrayList<>(replayConfigurationMap.values());
    }

    private void mergeGlobalComparisonConfig(Map<String, ReplayConfiguration.ReplayComparisonConfig> replayConfigurationMap,
                                             String appId) {
        if (replayConfigurationMap.containsKey(null)) {

            ReplayConfiguration.ReplayComparisonConfig globalConfig = replayConfigurationMap.get(null);
            Set<List<String>> globalExclusionList = globalConfig.getExclusionList();
            Set<List<String>> globalInclusionList = globalConfig.getInclusionList();

            List<String> operationIdList = getOperationIdList(appId);
            for (String operationId : operationIdList) {
                ReplayConfiguration.ReplayComparisonConfig tempReplayConfig = replayConfigurationMap.getOrDefault(
                        operationId, new ReplayConfiguration.ReplayComparisonConfig());
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

    private void buildExclusions(Map<String, ReplayConfiguration.ReplayComparisonConfig> replayConfigurationMap,
                                 List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurations) {
        if (CollectionUtils.isNotEmpty(comparisonExclusionsConfigurations)) {
            for (ComparisonExclusionsConfiguration compareExclusionsConfig : comparisonExclusionsConfigurations) {

                String operationId = compareExclusionsConfig.getOperationId();
                List<String> exclusions = compareExclusionsConfig.getExclusions();

                if (CollectionUtils.isNotEmpty(exclusions)) {
                    ReplayConfiguration.ReplayComparisonConfig tempReplayComparisonConfig =
                            replayConfigurationMap.getOrDefault(operationId, new ReplayConfiguration.ReplayComparisonConfig());
                    tempReplayComparisonConfig.setOperationId(operationId);

                    Set<List<String>> tempExclusionList = tempReplayComparisonConfig.getExclusionList() ==
                            null ? new HashSet<>() : tempReplayComparisonConfig.getExclusionList();
                    tempExclusionList.add(exclusions);
                    tempReplayComparisonConfig.setExclusionList(tempExclusionList);
                    replayConfigurationMap.put(operationId, tempReplayComparisonConfig);
                }
            }
        }
    }

    private void buildInclusions(Map<String, ReplayConfiguration.ReplayComparisonConfig> replayConfigurationMap,
                                 List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurations) {
        if (CollectionUtils.isNotEmpty(comparisonInclusionsConfigurations)) {
            for (ComparisonInclusionsConfiguration comparisonInclusionsConfiguration : comparisonInclusionsConfigurations) {

                String operationId = comparisonInclusionsConfiguration.getOperationId();
                List<String> inclusions = comparisonInclusionsConfiguration.getInclusions();

                if (CollectionUtils.isNotEmpty(inclusions)) {
                    ReplayConfiguration.ReplayComparisonConfig tempReplayComparisonConfig =
                            replayConfigurationMap.getOrDefault(operationId, new ReplayConfiguration.ReplayComparisonConfig());
                    tempReplayComparisonConfig.setOperationId(operationId);

                    Set<List<String>> tempInclusionList = tempReplayComparisonConfig.getInclusionList() ==
                            null ? new HashSet<>() : tempReplayComparisonConfig.getInclusionList();
                    tempInclusionList.add(inclusions);
                    tempReplayComparisonConfig.setInclusionList(tempInclusionList);
                    replayConfigurationMap.put(operationId, tempReplayComparisonConfig);
                }
            }
        }
    }

    private void buildListSort(Map<String, ReplayConfiguration.ReplayComparisonConfig> replayConfigurationMap,
                               List<ComparisonListSortConfiguration> comparisonListSortConfigurations) {
        if (CollectionUtils.isNotEmpty(comparisonListSortConfigurations)) {
            for (ComparisonListSortConfiguration compareListSortConfig : comparisonListSortConfigurations) {

                String operationId = compareListSortConfig.getOperationId();
                List<String> listPath = compareListSortConfig.getListPath();
                List<List<String>> keys = compareListSortConfig.getKeys();

                if (CollectionUtils.isNotEmpty(listPath) && CollectionUtils.isNotEmpty(keys)) {
                    ReplayConfiguration.ReplayComparisonConfig tempReplayComparisonConfig =
                            replayConfigurationMap.getOrDefault(operationId, new ReplayConfiguration.ReplayComparisonConfig());
                    tempReplayComparisonConfig.setOperationId(operationId);

                    Map<List<String>, List<List<String>>> tempListSortMap = tempReplayComparisonConfig.getListSortMap() ==
                            null ? new HashMap<>() : tempReplayComparisonConfig.getListSortMap();
                    tempListSortMap.put(listPath, keys);
                    tempReplayComparisonConfig.setListSortMap(tempListSortMap);
                    replayConfigurationMap.put(operationId, tempReplayComparisonConfig);
                }
            }
        }
    }

    private void buildReference(Map<String, ReplayConfiguration.ReplayComparisonConfig> replayConfigurationMap,
                                List<ComparisonReferenceConfiguration> comparisonReferenceConfigurations) {
        if (CollectionUtils.isNotEmpty(comparisonReferenceConfigurations)) {
            for (ComparisonReferenceConfiguration comparisonReferenceConfiguration : comparisonReferenceConfigurations) {

                String operationId = comparisonReferenceConfiguration.getOperationId();
                List<String> fkPath = comparisonReferenceConfiguration.getFkPath();
                List<String> pkPath = comparisonReferenceConfiguration.getPkPath();

                if (CollectionUtils.isNotEmpty(fkPath) && CollectionUtils.isNotEmpty(pkPath)) {
                    ReplayConfiguration.ReplayComparisonConfig tempReplayComparisonConfig =
                            replayConfigurationMap.getOrDefault(operationId, new ReplayConfiguration.ReplayComparisonConfig());
                    tempReplayComparisonConfig.setOperationId(operationId);

                    Map<List<String>, List<String>> tempReferenceMap = tempReplayComparisonConfig.getReferenceMap() ==
                            null ? new HashMap<>() : tempReplayComparisonConfig.getReferenceMap();
                    tempReferenceMap.put(fkPath, pkPath);
                    tempReplayComparisonConfig.setReferenceMap(tempReferenceMap);
                    replayConfigurationMap.put(operationId, tempReplayComparisonConfig);
                }
            }
        }
    }

}
