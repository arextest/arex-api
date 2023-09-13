package com.arextest.web.core.business.config.record;

import com.arextest.config.model.dto.record.DynamicClassConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import org.apache.commons.collections4.CollectionUtils;
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
        return CollectionUtils.isEmpty(super.useResultAsList(appId)) || repositoryProvider.removeByAppId(appId);
    }
}
