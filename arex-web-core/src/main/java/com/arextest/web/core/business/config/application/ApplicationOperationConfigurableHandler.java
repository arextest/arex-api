package com.arextest.web.core.business.config.application;

import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.ReplayDependencyRepository;
import com.arextest.web.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import com.arextest.web.model.dto.ReplayDependencyDto;
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

    @Autowired
    private ReplayDependencyRepository replayDependencyRepository;

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
        ApplicationOperationConfiguration result = applicationOperationConfigurationRepository.listByOperationId(operationId);
        ReplayDependencyDto replayDependencyDto = replayDependencyRepository.queryDependency(operationId);
        result.setDependencies(replayDependencyDto.getDependencies());
        return result;
    }
}
