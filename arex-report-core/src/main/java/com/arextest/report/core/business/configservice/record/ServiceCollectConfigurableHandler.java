package com.arextest.report.core.business.configservice.record;

import com.arextest.report.core.business.configservice.handler.AbstractConfigurableHandler;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.model.api.contracts.configservice.record.ServiceCollectConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author jmo
 * @since 2022/1/22
 */
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
        return Collections.singletonList(serviceCollectConfiguration);
    }


    @Override
    public boolean update(ServiceCollectConfiguration configuration) {
        return super.update(configuration) || super.insert(configuration);
    }

    @Override
    protected void mergeGlobalDefaultSettings(ServiceCollectConfiguration source) {
        Set<String> value = source.getExcludeDependentOperationSet();
        value = mergeValues(value, globalDefaultConfiguration.getExcludeDependentOperationSet());
        source.setExcludeDependentOperationSet(value);
        value = source.getExcludeDependentServiceSet();
        value = mergeValues(value, globalDefaultConfiguration.getExcludeDependentServiceSet());
        source.setExcludeDependentServiceSet(value);
        value = source.getExcludeOperationSet();
        value = mergeValues(value, globalDefaultConfiguration.getExcludeOperationSet());
        source.setExcludeOperationSet(value);
        value = source.getIncludeServiceSet();
        value = mergeValues(value, globalDefaultConfiguration.getIncludeServiceSet());
        source.setIncludeServiceSet(value);
        value = source.getIncludeOperationSet();
        value = mergeValues(value, globalDefaultConfiguration.getIncludeOperationSet());
        source.setIncludeOperationSet(value);
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

    @Configuration
    @ConfigurationProperties(prefix = "arex.config.default.service.collect")
    static class GlobalServiceCollectConfiguration extends ServiceCollectConfiguration {

    }
}
