package com.arextest.report.core.business.config.application;

import com.arextest.report.core.business.config.handler.AbstractConfigurableHandler;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.report.model.api.contracts.config.application.ApplicationOperationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Component
public final class ApplicationOperationConfigurableHandler extends AbstractConfigurableHandler<ApplicationOperationConfiguration> {

    protected ApplicationOperationConfigurableHandler(@Autowired ConfigRepositoryProvider<ApplicationOperationConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Autowired
    ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

    @Override
    public boolean insert(ApplicationOperationConfiguration configuration) {
        if (configuration.getServiceId() == null) {
            return false;
        }
        if (StringUtils.isEmpty(configuration.getOperationName())) {
            return false;
        }
        return super.insert(configuration);
    }

    public ApplicationOperationConfiguration useResultById(String operationId) {
        return applicationOperationConfigurationRepository.listByOperationId(operationId);
    }
}
