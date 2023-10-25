package com.arextest.web.core.business.config.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arextest.config.model.dto.application.ApplicationServiceConfiguration;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.core.business.config.AbstractConfigurableHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Slf4j
@Component
public final class ApplicationServiceConfigurableHandler
    extends AbstractConfigurableHandler<ApplicationServiceConfiguration> {

    protected ApplicationServiceConfigurableHandler(
        @Autowired ConfigRepositoryProvider<ApplicationServiceConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    public boolean insert(ApplicationServiceConfiguration configuration) {
        // note: this method is not supported in arex-api, it is in storage-api
        throw new UnsupportedOperationException("insert is not supported");
    }
}
