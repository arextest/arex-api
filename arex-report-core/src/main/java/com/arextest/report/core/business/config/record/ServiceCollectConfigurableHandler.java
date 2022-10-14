package com.arextest.report.core.business.config.record;

import com.arextest.report.core.business.config.handler.AbstractConfigurableHandler;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.model.api.contracts.config.record.ServiceCollectConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Slf4j
@Component
final class ServiceCollectConfigurableHandler extends AbstractConfigurableHandler<ServiceCollectConfiguration> {

    @Resource
    private ServiceCollectConfiguration globalDefaultConfiguration;

    protected ServiceCollectConfigurableHandler(@Autowired ConfigRepositoryProvider<ServiceCollectConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    protected List<ServiceCollectConfiguration> createFromGlobalDefault(String appId) {
        ServiceCollectConfiguration serviceCollectConfiguration = new ServiceCollectConfiguration();
        serviceCollectConfiguration.setAppId(appId);
        serviceCollectConfiguration.setSampleRate(globalDefaultConfiguration.getSampleRate());
        serviceCollectConfiguration.setAllowDayOfWeeks(globalDefaultConfiguration.getAllowDayOfWeeks());
        serviceCollectConfiguration.setTimeMock(globalDefaultConfiguration.isTimeMock());
        serviceCollectConfiguration.setAllowTimeOfDayFrom(globalDefaultConfiguration.getAllowTimeOfDayFrom());
        serviceCollectConfiguration.setAllowTimeOfDayTo(globalDefaultConfiguration.getAllowTimeOfDayTo());
        serviceCollectConfiguration.setExcludeOperationMap(mergeValues(new HashMap<>(), globalDefaultConfiguration.getExcludeOperationMap()));
        return Collections.singletonList(serviceCollectConfiguration);
    }


    @Override
    public boolean update(ServiceCollectConfiguration configuration) {
        return super.update(configuration) || super.insert(configuration);
    }

    @Override
    protected void mergeGlobalDefaultSettings(ServiceCollectConfiguration source) {
        Map<String, Set<String>> excludeOperationMap = source.getExcludeOperationMap();
        excludeOperationMap = mergeValues(excludeOperationMap, globalDefaultConfiguration.getExcludeOperationMap());
        source.setExcludeOperationMap(excludeOperationMap);
    }

    @Override
    protected boolean shouldMergeGlobalDefault() {
        return true;
    }

    private <T> Set<T> mergeValues(Set<T> source, Set<T> globalValues) {
        if (CollectionUtils.isEmpty(globalValues)) {
            return source;
        }
        if (CollectionUtils.isEmpty(source)) {
            return globalValues;
        }
        source.addAll(globalValues);
        return source;
    }


    private <K, V, U> Map<K, Set<V>> mergeValues(Map<K, Set<V>> source, Map<K, U> globalValues) {
        if (globalValues == null || globalValues.isEmpty()) {
            return source;
        }
        Map<K, Set<V>> valueMap = Optional.ofNullable(source).orElse(new HashMap<>());
        globalValues.forEach((k, v) -> {
            Set<V> orDefault = valueMap.getOrDefault(k, new HashSet<>());
            if (v != null) {
                orDefault.addAll((List) v);
            }
            valueMap.put(k, orDefault);
        });
        return valueMap;
    }

    @Configuration
    @ConfigurationProperties(prefix = "arex.config.default.service.collect")
    static class GlobalServiceCollectConfiguration extends ServiceCollectConfiguration {

    }
}
