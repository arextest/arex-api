package com.arextest.web.core.business.config.application;

import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.core.repository.mongo.InstancesConfigurationRepositoryImpl;
import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Slf4j
@Component
public final class ApplicationInstancesConfigurableHandler extends AbstractConfigurableHandler<InstancesConfiguration> {

    protected ApplicationInstancesConfigurableHandler(
            @Autowired ConfigRepositoryProvider<InstancesConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Resource
    private InstancesConfigurationRepositoryImpl instancesConfigurationRepository;

    public void createOrUpdate(InstancesConfiguration instancesConfiguration) {
        super.update(instancesConfiguration);
    }

    private void create(InstancesConfiguration instancesConfiguration) {
        super.insert(instancesConfiguration);
    }

    public List<InstancesConfiguration> useResultAsList(String appId, int top) {
        return instancesConfigurationRepository.listBy(appId, top);
    }

    public List<InstancesConfiguration> useResultAsList(String appId) {
        return instancesConfigurationRepository.listBy(appId);
    }
}