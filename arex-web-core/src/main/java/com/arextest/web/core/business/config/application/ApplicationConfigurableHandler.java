package com.arextest.web.core.business.config.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arextest.config.model.dto.application.ApplicationConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Component
class ApplicationConfigurableHandler extends AbstractConfigurableHandler<ApplicationConfiguration> {

    protected ApplicationConfigurableHandler(
        @Autowired ConfigRepositoryProvider<ApplicationConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    public boolean insert(ApplicationConfiguration configuration) {
        // note: this method is not supported in arex-api, it is in storage-api
        throw new UnsupportedOperationException("insert is not supported");
    }
}
