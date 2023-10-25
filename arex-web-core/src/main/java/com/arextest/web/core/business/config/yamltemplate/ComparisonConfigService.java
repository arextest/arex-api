package com.arextest.web.core.business.config.yamltemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.arextest.config.model.dto.application.ApplicationServiceConfiguration;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonExclusionsConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonInclusionsConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonListSortConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonReferenceConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.AbstractComparisonDetailsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonListSortConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.ListSortTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.OperationCompareTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.ReferenceTemplateConfig;
import com.arextest.web.model.contract.contracts.config.yamlTemplate.entity.YamlTemplate;

@Component
public class ComparisonConfigService {

    @Resource
    ConfigurableHandler<ApplicationServiceConfiguration> applicationServiceConfigurableHandler;

    @Resource
    ComparisonExclusionsConfigurableHandler comparisonExclusionsConfigurableHandler;

    @Resource
    ComparisonInclusionsConfigurableHandler comparisonInclusionsConfigurableHandler;

    @Resource
    ComparisonListSortConfigurableHandler comparisonListSortConfigurableHandler;

    @Resource
    ComparisonReferenceConfigurableHandler comparisonReferenceConfigurableHandler;

    public List<OperationCompareTemplateConfig> getCompareConfig(String appId) {
        // k:operationId
        Map<String, OperationCompareTemplateConfig> operationCompareConfigMap = new HashMap<>();

        // k:operationId
        Map<String, String> operationIdToName = new HashMap<>();
        // global
        operationIdToName.put(null, null);
        List<ApplicationServiceConfiguration> applicationServiceConfigurations =
            applicationServiceConfigurableHandler.useResultAsList(appId);
        Optional.ofNullable(applicationServiceConfigurations).orElse(Collections.emptyList()).forEach(service -> {
            Optional.ofNullable(service.getOperationList()).orElse(Collections.emptyList()).forEach(operation -> {
                operationIdToName.put(operation.getId(), operation.getOperationName());
            });
        });

        getComparisonExclusions(appId, operationIdToName, operationCompareConfigMap);
        getComparisonInclusions(appId, operationIdToName, operationCompareConfigMap);
        getComparisonListSort(appId, operationIdToName, operationCompareConfigMap);
        getComparisonReference(appId, operationIdToName, operationCompareConfigMap);

        return new ArrayList<>(operationCompareConfigMap.values());
    }

    public boolean updateComparison(YamlTemplate template, String appId) {

        // k:operationId
        Map<String, String> operationNameToId = new HashMap<>();
        // global
        operationNameToId.put(null, null);
        List<ApplicationServiceConfiguration> applicationServiceConfigurations =
            applicationServiceConfigurableHandler.useResultAsList(appId);
        Optional.ofNullable(applicationServiceConfigurations).orElse(Collections.emptyList()).forEach(service -> {
            Optional.ofNullable(service.getOperationList()).orElse(Collections.emptyList()).forEach(operation -> {
                operationNameToId.put(operation.getOperationName(), operation.getId());
            });
        });
        List<OperationCompareTemplateConfig> compareConfig = template.getCompareTemplateConfigs();

        return updateComparisonExclusions(appId, operationNameToId, compareConfig)
            && updateComparisonInclusions(appId, operationNameToId, compareConfig)
            && updateComparisonListSort(appId, operationNameToId, compareConfig)
            && updateComparisonReference(appId, operationNameToId, compareConfig);
    }

    private void getComparisonExclusions(String appId, Map<String, String> operationIdToName,
        Map<String, OperationCompareTemplateConfig> operationCompareConfigMap) {
        List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurations =
            comparisonExclusionsConfigurableHandler.useResultAsList(appId);
        Map<String, List<ComparisonExclusionsConfiguration>> comparisonExclusionsMap =
            comparisonListGroupBy(comparisonExclusionsConfigurations);

        for (Map.Entry<String, String> entry : operationIdToName.entrySet()) {
            String operationId = entry.getKey();
            String operationName = entry.getValue();

            if (comparisonExclusionsMap.containsKey(operationId)) {
                List<List<String>> exclusions = comparisonExclusionsMap.get(operationId).stream()
                    .map(ComparisonExclusionsConfiguration::getExclusions).collect(Collectors.toList());
                OperationCompareTemplateConfig tempOperationCompareTemplateConfig =
                    operationCompareConfigMap.getOrDefault(operationId, new OperationCompareTemplateConfig());
                tempOperationCompareTemplateConfig.setOperationName(operationName);
                tempOperationCompareTemplateConfig.setExclusions(FormatPath.formatMultiPath(exclusions));
                operationCompareConfigMap.put(operationId, tempOperationCompareTemplateConfig);
            }
        }
    }

    private void getComparisonInclusions(String appId, Map<String, String> operationIdToName,
        Map<String, OperationCompareTemplateConfig> operationCompareConfigMap) {
        List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurations =
            comparisonInclusionsConfigurableHandler.useResultAsList(appId);
        Map<String, List<ComparisonInclusionsConfiguration>> comparisonInclusionsMap =
            comparisonListGroupBy(comparisonInclusionsConfigurations);

        for (Map.Entry<String, String> entry : operationIdToName.entrySet()) {
            String operationId = entry.getKey();
            String operationName = entry.getValue();

            if (comparisonInclusionsMap.containsKey(operationId)) {
                List<List<String>> inclusions = comparisonInclusionsMap.get(operationId).stream()
                    .map(ComparisonInclusionsConfiguration::getInclusions).collect(Collectors.toList());
                OperationCompareTemplateConfig tempOperationCompareTemplateConfig =
                    operationCompareConfigMap.getOrDefault(operationId, new OperationCompareTemplateConfig());
                tempOperationCompareTemplateConfig.setOperationName(operationName);
                tempOperationCompareTemplateConfig.setInclusions(FormatPath.formatMultiPath(inclusions));
                operationCompareConfigMap.put(operationId, tempOperationCompareTemplateConfig);
            }
        }

    }

    private void getComparisonListSort(String appId, Map<String, String> operationIdToName,
        Map<String, OperationCompareTemplateConfig> operationCompareConfigMap) {
        List<ComparisonListSortConfiguration> comparisonListSortConfigurations =
            comparisonListSortConfigurableHandler.useResultAsList(appId);
        Map<String, List<ComparisonListSortConfiguration>> comparisonListSortMap =
            comparisonListGroupBy(comparisonListSortConfigurations);

        for (Map.Entry<String, String> entry : operationIdToName.entrySet()) {
            String operationId = entry.getKey();
            String operationName = entry.getValue();

            if (comparisonListSortMap.containsKey(operationId)) {
                List<ListSortTemplateConfig> listSortTemplateConfigs =
                    comparisonListSortMap.get(operationId).stream().map(item -> {
                        ListSortTemplateConfig listSortTemplateConfig = new ListSortTemplateConfig();
                        listSortTemplateConfig.setListPath(FormatPath.formatPath(item.getListPath()));
                        listSortTemplateConfig.setKeys(FormatPath.formatMultiPath(item.getKeys()));
                        return listSortTemplateConfig;
                    }).collect(Collectors.toList());

                OperationCompareTemplateConfig tempOperationCompareTemplateConfig =
                    operationCompareConfigMap.getOrDefault(operationId, new OperationCompareTemplateConfig());
                tempOperationCompareTemplateConfig.setOperationName(operationName);
                tempOperationCompareTemplateConfig.setListSort(listSortTemplateConfigs);
                operationCompareConfigMap.put(operationId, tempOperationCompareTemplateConfig);
            }
        }
    }

    private void getComparisonReference(String appId, Map<String, String> operationIdToName,
        Map<String, OperationCompareTemplateConfig> operationCompareConfigMap) {
        List<ComparisonReferenceConfiguration> comparisonReferenceConfigurations =
            comparisonReferenceConfigurableHandler.useResultAsList(appId);
        Map<String, List<ComparisonReferenceConfiguration>> comparisonReferenceMap =
            comparisonListGroupBy(comparisonReferenceConfigurations);

        for (Map.Entry<String, String> entry : operationIdToName.entrySet()) {
            String operationId = entry.getKey();
            String operationName = entry.getValue();
            if (comparisonReferenceMap.containsKey(operationId)) {
                List<ReferenceTemplateConfig> referenceTemplateConfigs =
                    comparisonReferenceMap.get(operationId).stream().map(item -> {
                        ReferenceTemplateConfig referenceTemplateConfig = new ReferenceTemplateConfig();
                        referenceTemplateConfig.setFkPath(FormatPath.formatPath(item.getFkPath()));
                        referenceTemplateConfig.setPkPath(FormatPath.formatPath(item.getPkPath()));
                        return referenceTemplateConfig;
                    }).collect(Collectors.toList());

                OperationCompareTemplateConfig tempOperationCompareTemplateConfig =
                    operationCompareConfigMap.getOrDefault(operationId, new OperationCompareTemplateConfig());
                tempOperationCompareTemplateConfig.setOperationName(operationName);
                tempOperationCompareTemplateConfig.setReferences(referenceTemplateConfigs);
                operationCompareConfigMap.put(operationId, tempOperationCompareTemplateConfig);
            }
        }
    }

    private <T extends AbstractComparisonDetailsConfiguration> Map<String, List<T>>
        comparisonListGroupBy(List<T> comparisonList) {
        Map<String, List<T>> comparisonMap = new HashMap<>();
        Optional.ofNullable(comparisonList).orElse(Collections.emptyList()).forEach(item -> {
            String operationId = item.getOperationId();
            List<T> orDefault = comparisonMap.getOrDefault(operationId, new ArrayList<>());
            orDefault.add(item);
            comparisonMap.put(operationId, orDefault);
        });
        return comparisonMap;
    }

    private boolean updateComparisonExclusions(String appId, Map<String, String> operationNameToId,
        List<OperationCompareTemplateConfig> compareConfig) {
        // remove
        if (!comparisonExclusionsConfigurableHandler.removeByAppId(appId)) {
            return false;
        }

        // insert
        List<OperationCompareTemplateConfig> operationCompareTemplateConfigs =
            Optional.ofNullable(compareConfig).orElse(Collections.emptyList()).stream()
                .filter(item -> CollectionUtils.isNotEmpty(item.getExclusions())).collect(Collectors.toList());
        for (OperationCompareTemplateConfig operationCompareTemplateConfig : operationCompareTemplateConfigs) {
            List<List<String>> exclusionCollection =
                FormatPath.parseMultiPath(operationCompareTemplateConfig.getExclusions());
            List<ComparisonExclusionsConfiguration> comparisonExclusionsConfigurations =
                Optional.ofNullable(exclusionCollection).orElse(Collections.emptyList()).stream()
                    .filter(ValidUtils::isValid).map(exclusions -> {
                        ComparisonExclusionsConfiguration comparisonExclusionsConfiguration =
                            new ComparisonExclusionsConfiguration();
                        comparisonExclusionsConfiguration.setAppId(appId);
                        comparisonExclusionsConfiguration
                            .setOperationId(operationNameToId.get(operationCompareTemplateConfig.getOperationName()));
                        comparisonExclusionsConfiguration.setExclusions(exclusions);
                        return comparisonExclusionsConfiguration;
                    }).collect(Collectors.toList());
            if (!comparisonExclusionsConfigurableHandler.insertList(comparisonExclusionsConfigurations)) {
                return false;
            }
        }
        return true;
    }

    private boolean updateComparisonInclusions(String appId, Map<String, String> operationNameToId,
        List<OperationCompareTemplateConfig> compareConfig) {
        // remove
        if (!comparisonInclusionsConfigurableHandler.removeByAppId(appId)) {
            return false;
        }

        // insert
        List<OperationCompareTemplateConfig> operationCompareTemplateConfigs =
            Optional.ofNullable(compareConfig).orElse(Collections.emptyList()).stream()
                .filter(item -> CollectionUtils.isNotEmpty(item.getInclusions())).collect(Collectors.toList());

        for (OperationCompareTemplateConfig operationCompareTemplateConfig : operationCompareTemplateConfigs) {
            List<List<String>> inclusionCollection =
                FormatPath.parseMultiPath(operationCompareTemplateConfig.getInclusions());
            List<ComparisonInclusionsConfiguration> comparisonInclusionsConfigurations =
                Optional.ofNullable(inclusionCollection).orElse(Collections.emptyList()).stream()
                    .filter(ValidUtils::isValid).map(inclusions -> {
                        ComparisonInclusionsConfiguration comparisonInclusionsConfiguration =
                            new ComparisonInclusionsConfiguration();
                        comparisonInclusionsConfiguration.setAppId(appId);
                        comparisonInclusionsConfiguration
                            .setOperationId(operationNameToId.get(operationCompareTemplateConfig.getOperationName()));
                        comparisonInclusionsConfiguration.setInclusions(inclusions);
                        return comparisonInclusionsConfiguration;
                    }).collect(Collectors.toList());
            if (!comparisonInclusionsConfigurableHandler.insertList(comparisonInclusionsConfigurations)) {
                return false;
            }
        }
        return true;
    }

    private boolean updateComparisonListSort(String appId, Map<String, String> operationNameToId,
        List<OperationCompareTemplateConfig> compareConfig) {
        // remove
        if (!comparisonListSortConfigurableHandler.removeByAppId(appId)) {
            return false;
        }

        // insert
        List<OperationCompareTemplateConfig> operationCompareTemplateConfigs =
            Optional.ofNullable(compareConfig).orElse(Collections.emptyList()).stream()
                .filter(item -> CollectionUtils.isNotEmpty(item.getListSort())).collect(Collectors.toList());
        for (OperationCompareTemplateConfig operationCompareTemplateConfig : operationCompareTemplateConfigs) {
            List<ComparisonListSortConfiguration> comparisonListSortConfigurations = new ArrayList<>();
            Collection<ListSortTemplateConfig> listSortTemplateConfigs = operationCompareTemplateConfig.getListSort();
            for (ListSortTemplateConfig listSortTemplateConfig : listSortTemplateConfigs) {
                List<String> listPath = FormatPath.parsePath(listSortTemplateConfig.getListPath());
                List<List<String>> keys = FormatPath.parseMultiPath(listSortTemplateConfig.getKeys());
                if (ValidUtils.isValid(listPath) && ValidUtils.isMultiValid(keys)) {
                    ComparisonListSortConfiguration comparisonListSortConfiguration =
                        new ComparisonListSortConfiguration();
                    comparisonListSortConfiguration.setAppId(appId);
                    comparisonListSortConfiguration
                        .setOperationId(operationNameToId.get(operationCompareTemplateConfig.getOperationName()));
                    comparisonListSortConfiguration.setListPath(listPath);
                    comparisonListSortConfiguration.setKeys(keys);
                    comparisonListSortConfigurations.add(comparisonListSortConfiguration);
                }
            }
            if (!comparisonListSortConfigurableHandler.insertList(comparisonListSortConfigurations)) {
                return false;
            }
        }
        return true;
    }

    private boolean updateComparisonReference(String appId, Map<String, String> operationNameToId,
        List<OperationCompareTemplateConfig> compareConfigs) {
        // remove
        if (!comparisonReferenceConfigurableHandler.removeByAppId(appId)) {
            return false;
        }

        // insert
        List<OperationCompareTemplateConfig> operationCompareTemplateConfigs =
            Optional.ofNullable(compareConfigs).orElse(Collections.emptyList()).stream()
                .filter(item -> CollectionUtils.isNotEmpty(item.getReferences())).collect(Collectors.toList());
        for (OperationCompareTemplateConfig operationCompareTemplateConfig : operationCompareTemplateConfigs) {
            List<ComparisonReferenceConfiguration> comparisonReferenceConfigurations = new ArrayList<>();
            Collection<ReferenceTemplateConfig> referenceTemplateConfigs =
                operationCompareTemplateConfig.getReferences();
            for (ReferenceTemplateConfig referenceTemplateConfig : referenceTemplateConfigs) {
                List<String> fkPath = FormatPath.parsePath(referenceTemplateConfig.getFkPath());
                List<String> pkPath = FormatPath.parsePath(referenceTemplateConfig.getPkPath());
                if (ValidUtils.isValid(fkPath) && ValidUtils.isValid(pkPath)) {
                    ComparisonReferenceConfiguration comparisonReferenceConfiguration =
                        new ComparisonReferenceConfiguration();
                    comparisonReferenceConfiguration.setAppId(appId);
                    comparisonReferenceConfiguration
                        .setOperationId(operationNameToId.get(operationCompareTemplateConfig.getOperationName()));
                    comparisonReferenceConfiguration.setFkPath(fkPath);
                    comparisonReferenceConfiguration.setPkPath(pkPath);
                    comparisonReferenceConfigurations.add(comparisonReferenceConfiguration);
                }
            }
            if (!comparisonReferenceConfigurableHandler.insertList(comparisonReferenceConfigurations)) {
                return false;
            }
        }
        return true;
    }

    private static final class FormatPath {

        private static List<String> formatMultiPath(Collection<List<String>> paths) {
            if (CollectionUtils.isEmpty(paths)) {
                return null;
            }
            return paths.stream().filter(CollectionUtils::isNotEmpty).map(FormatPath::formatPath)
                .collect(Collectors.toList());
        }

        private static List<List<String>> parseMultiPath(Collection<String> paths) {
            if (CollectionUtils.isEmpty(paths)) {
                return null;
            }
            return paths.stream().filter(StringUtils::isNotEmpty).map(FormatPath::parsePath)
                .collect(Collectors.toList());
        }

        private static String formatPath(List<String> path) {
            if (CollectionUtils.isEmpty(path)) {
                return null;
            }
            return String.join("/", path);
        }

        private static List<String> parsePath(String path) {
            if (StringUtils.isEmpty(path)) {
                return null;
            }
            return Arrays.stream(path.split("/")).collect(Collectors.toList());
        }

    }

    private static final class ValidUtils {

        private static boolean isValid(Collection<String> config) {
            return CollectionUtils.isNotEmpty(config) && !config.contains("");
        }

        private static boolean isMultiValid(Collection<List<String>> configs) {
            if (CollectionUtils.isEmpty(configs)) {
                return false;
            }
            for (List<String> config : configs) {
                if (!isValid(config)) {
                    return false;
                }
            }
            return true;
        }
    }
}
