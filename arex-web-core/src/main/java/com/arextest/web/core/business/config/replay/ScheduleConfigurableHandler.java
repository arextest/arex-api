package com.arextest.web.core.business.config.replay;

import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.ServiceCollectConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.record.ServiceCollectConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Component
public final class ScheduleConfigurableHandler extends AbstractConfigurableHandler<ScheduleConfiguration> {
    @Resource
    private ScheduleConfiguration globalScheduleConfiguration;

    @Resource
    private ServiceCollectConfigurationRepositoryImpl serviceCollectConfigurationRepositoryImpl;


    protected ScheduleConfigurableHandler(@Autowired ConfigRepositoryProvider<ScheduleConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    public boolean update(ScheduleConfiguration configuration) {
        if (!super.update(configuration)) {
            mergeGlobalDefaultSettings(configuration);
            return super.insert(configuration);
        }
        return true;
    }

    @Override
    public List<ScheduleConfiguration> createFromGlobalDefault(String appId) {
        ScheduleConfiguration scheduleConfiguration = new ScheduleConfiguration();
        scheduleConfiguration.setAppId(appId);
        scheduleConfiguration.setOffsetDays(globalScheduleConfiguration.getOffsetDays());
        scheduleConfiguration.setSendMaxQps(globalScheduleConfiguration.getSendMaxQps());
        scheduleConfiguration.setTargetEnv(globalScheduleConfiguration.getTargetEnv());
        scheduleConfiguration.setExcludeServiceOperationSet(getExcludeServiceOperationSet(appId));
        return Collections.singletonList(scheduleConfiguration);
    }

    @Override
    protected boolean shouldMergeGlobalDefault() {
        return false;
    }

    @Configuration
    @ConfigurationProperties(prefix = "arex.config.default.schedule")
    static class GlobalScheduleConfiguration extends ScheduleConfiguration {

    }

    @Override
    protected void mergeGlobalDefaultSettings(ScheduleConfiguration source) {
        if (source.getOffsetDays() == null) {
            source.setOffsetDays(globalScheduleConfiguration.getOffsetDays());
        }
        if (source.getSendMaxQps() == null) {
            source.setSendMaxQps(globalScheduleConfiguration.getSendMaxQps());
        }
        if (source.getTargetEnv() == null) {
            source.setTargetEnv(globalScheduleConfiguration.getTargetEnv());
        }
        if (source.getExcludeServiceOperationSet() == null) {
            source.setExcludeServiceOperationSet(getExcludeServiceOperationSet(source.getAppId()));
        }
    }
    private Set<String> getExcludeServiceOperationSet(String appId) {
        List<ServiceCollectConfiguration> list = serviceCollectConfigurationRepositoryImpl.listBy(appId);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptySet();
        }
        return list.get(0).getExcludeServiceOperationSet();
    }
}