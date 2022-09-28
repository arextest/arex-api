package com.arextest.report.core.business.configservice.schedule;

import com.arextest.report.core.business.configservice.handler.AbstractConfigurableHandler;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.RepositoryProvider;
import com.arextest.report.model.api.contracts.configservice.replay.ScheduleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import sun.security.krb5.Config;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Component
final class ScheduleConfigurableHandler extends AbstractConfigurableHandler<ScheduleConfiguration> {
    @Resource
    private ScheduleConfiguration globalScheduleConfiguration;

    protected ScheduleConfigurableHandler(@Autowired ConfigRepositoryProvider<ScheduleConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    public boolean update(ScheduleConfiguration configuration) {
        return super.update(configuration) || super.insert(configuration);
    }

    @Override
    protected List<ScheduleConfiguration> createFromGlobalDefault(String appId) {
        ScheduleConfiguration scheduleConfiguration = new ScheduleConfiguration();
        scheduleConfiguration.setAppId(appId);
        scheduleConfiguration.setOffsetDays(globalScheduleConfiguration.getOffsetDays());
        scheduleConfiguration.setSendMaxQps(globalScheduleConfiguration.getSendMaxQps());
        scheduleConfiguration.setTargetEnv(globalScheduleConfiguration.getTargetEnv());
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

}
