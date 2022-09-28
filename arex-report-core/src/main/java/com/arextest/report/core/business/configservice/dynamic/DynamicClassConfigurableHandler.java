package com.arextest.report.core.business.configservice.dynamic;

import com.arextest.report.core.business.configservice.handler.AbstractConfigurableHandler;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.model.api.contracts.configservice.record.DynamicClassConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Component
public final class DynamicClassConfigurableHandler extends AbstractConfigurableHandler<DynamicClassConfiguration> {
    protected DynamicClassConfigurableHandler(@Autowired ConfigRepositoryProvider<DynamicClassConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    public boolean insert(DynamicClassConfiguration configuration) {
        if (StringUtils.isEmpty(configuration.getAppId())) {
            return false;
        }
        if (StringUtils.isEmpty(configuration.getFullClassName())) {
            return false;
        }
        return super.insert(configuration);
    }

    public boolean removeByAppId(String appId) {
        return super.useResultAsList(appId).isEmpty() || repositoryProvider.removeByAppId(appId);
    }
}
