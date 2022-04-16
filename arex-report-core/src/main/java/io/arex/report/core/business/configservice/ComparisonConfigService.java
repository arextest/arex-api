package io.arex.report.core.business.configservice;

import io.arex.report.core.business.config.ApplicationProperties;
import io.arex.report.core.business.util.ConfigServiceUtils;
import io.arex.report.model.api.contracts.configservice.*;
import io.arex.report.model.mapper.ComparisonDetailsMapper;
import io.arex.report.model.mapper.ComparisonMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static io.arex.report.core.business.util.ConfigServiceUtils.produceListEntity;
import static io.arex.report.core.business.util.ConfigServiceUtils.sendGetHttpRequest;


@Component
public class ComparisonConfigService {


    private static final String COMPARISON_URL = "/config/comparison/useResultAsList/appId/";
    private static final String COMPARISON_REMOVE_URL = "/config/comparison/modify/REMOVE";
    private static final String COMPARISON_INSERT_URL = "/config/comparison/modify/INSERT";

    @Resource
    private ApplicationProperties applicationProperties;

    public CompareConfig getCompareConfig(String appId) {
        List<ComparisonConfiguration> totalCompareConfig = getTotalCompareConfig(appId);
        return ComparisonMapper.INSTANCE.fromConfig(totalCompareConfig);
    }


    public boolean updateComparison(ConfigTemplate template, String appId) {
        List<ComparisonConfiguration> comparisonConfigurations = ComparisonMapper.INSTANCE.toConfig(template.getCompareConfig());
        List<ComparisonConfiguration> totalCompareConfig = getTotalCompareConfig(appId);
        Map<Integer, ComparisonConfiguration> updateMap = findUpdateEntity(comparisonConfigurations, appId);
        Map<Integer, ComparisonConfiguration> removeMap = findRemoveEntity(totalCompareConfig, appId);
        return updateComparisonDetails(updateMap, removeMap, 0)
                && updateComparisonDetails(updateMap, removeMap, 7)
                && updateComparisonDetails(updateMap, removeMap, 4)
                && updateComparisonDetails(updateMap, removeMap, 5);
    }


    private boolean updateComparisonDetails(Map<Integer, ComparisonConfiguration> updateMap, Map<Integer, ComparisonConfiguration> removeMap, int categoryType) {
        ComparisonConfiguration removeEntity = removeMap.get(categoryType);
        ComparisonConfiguration updateEntity = updateMap.get(categoryType);
        boolean result = true;
        if (removeEntity != null && removeEntity.getDetailsList() != null && !removeEntity.getDetailsList().isEmpty()) {
            result = result && ConfigServiceUtils.sendPostHttpRequest(applicationProperties.getConfigServiceUrl() + COMPARISON_REMOVE_URL, removeEntity);
        }
        if (updateEntity != null && updateEntity.getDetailsList() != null && !updateEntity.getDetailsList().isEmpty()) {
            result = result && ConfigServiceUtils.sendPostHttpRequest(applicationProperties.getConfigServiceUrl() + COMPARISON_INSERT_URL, updateEntity);
        }
        return result;
    }

    private List<ComparisonConfiguration> getTotalCompareConfig(String appId) {
        String url = applicationProperties.getConfigServiceUrl() + COMPARISON_URL + appId;
        return produceListEntity(sendGetHttpRequest(url), ComparisonConfiguration.class);
    }

    private Map<Integer, ComparisonConfiguration> findRemoveEntity(List<ComparisonConfiguration> comparisonConfigurations, String appId) {
        Map<Integer, ComparisonConfiguration> result = new HashMap<>();
        if (comparisonConfigurations == null) {
            return result;
        }
        comparisonConfigurations.stream().collect(Collectors.groupingBy(ComparisonConfiguration::getCategoryType)).forEach((k, v) -> {
            ComparisonConfiguration comparisonConfiguration = v.get(0);
            if (comparisonConfiguration != null) {
                result.put(k, comparisonConfiguration);
            }
        });
        return result;
    }

    private Map<Integer, ComparisonConfiguration> findUpdateEntity(List<ComparisonConfiguration> comparisonConfigurations, String appId) {
        Map<Integer, ComparisonConfiguration> result = new HashMap<>();
        if (comparisonConfigurations == null || comparisonConfigurations.isEmpty()) {
            return result;
        }
        comparisonConfigurations.stream().filter(Objects::nonNull).forEach(item -> {
            item.setAppId(appId);
        });
        comparisonConfigurations.stream().collect(Collectors.groupingBy(ComparisonConfiguration::getCategoryType)).forEach((k, v) -> {
            ComparisonConfiguration comparisonConfiguration = v.get(0);
            if (comparisonConfiguration != null) {
                result.put(k, comparisonConfiguration);
            }
        });
        return result;
    }

    public static void main(String[] args) {
        ComparisonDetailsConfiguration detailsConfiguration = new ComparisonDetailsConfiguration();
        detailsConfiguration.setPathName("test");
        detailsConfiguration.setPathValue(Arrays.asList("a/test", "b/test"));
        ComparisonDetails comparisonDetails = ComparisonDetailsMapper.INSTANCE.detailsFormConfig(detailsConfiguration);
        ComparisonDetailsConfiguration detailsConfiguration1 = ComparisonDetailsMapper.INSTANCE.configFromDetails(comparisonDetails);
    }


}
