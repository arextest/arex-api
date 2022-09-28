package com.arextest.report.core.business.configservice.application;

import com.arextest.report.core.business.configservice.handler.AbstractConfigurableHandler;
import com.arextest.report.core.repository.ConfigRepositoryProvider;
import com.arextest.report.model.api.contracts.configservice.application.ApplicationOperationConfiguration;
import com.arextest.report.model.api.contracts.configservice.record.ServiceCollectConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Component
final class ApplicationOperationConfigurableHandler extends AbstractConfigurableHandler<ApplicationOperationConfiguration> {
    @Resource
    private ServiceCollectConfiguration globalDefaultConfiguration;

    protected ApplicationOperationConfigurableHandler(@Autowired ConfigRepositoryProvider<ApplicationOperationConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    public boolean insert(ApplicationOperationConfiguration configuration) {
        if (configuration.getServiceId() == null) {
            return false;
        }
        if (StringUtils.isEmpty(configuration.getOperationName())) {
            return false;
        }
        if (isExcludedOperation(configuration.getOperationName())) {
            return true;
        }
        if (isIncludedOperation(configuration.getOperationName())) {
            return super.insert(configuration);
        }
        return true;
    }

    private boolean isIncludedOperation(String operationName) {
        return super.isIncluded(globalDefaultConfiguration.getIncludeOperationSet(), operationName);
    }

    private boolean isExcludedOperation(String operationName) {
        return super.isExcluded(globalDefaultConfiguration.getExcludeOperationSet(), operationName);
    }
}
