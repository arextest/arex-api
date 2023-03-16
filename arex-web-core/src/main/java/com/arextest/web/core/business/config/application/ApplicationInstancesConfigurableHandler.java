package com.arextest.web.core.business.config.application;

import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Slf4j
@Component
public final class ApplicationInstancesConfigurableHandler extends AbstractConfigurableHandler<InstancesConfiguration> {

    protected ApplicationInstancesConfigurableHandler(@Autowired ConfigRepositoryProvider<InstancesConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    public void createOrUpdate(String appId, String host, String recordVersion) {
        List<InstancesConfiguration> instancesConfigurations = super.useResultAsList(appId);
        if (CollectionUtils.isEmpty(instancesConfigurations)) {
            create(appId, host, recordVersion);
        } else {
            Optional<InstancesConfiguration> first = instancesConfigurations.stream().filter(instancesConfiguration -> instancesConfiguration.getHost().equals(host)).findFirst();
            if (first.isPresent()) {
                InstancesConfiguration instancesConfiguration = first.get();
                instancesConfiguration.setRecordVersion(recordVersion);
                instancesConfiguration.setDataUpdateTime(new Date());
                super.update(instancesConfiguration);
            } else {
                create(appId, host, recordVersion);
            }
        }
    }

    private void create(String appId, String host, String recordVersion) {
        InstancesConfiguration instancesConfiguration = new InstancesConfiguration();
        instancesConfiguration.setAppId(appId);
        instancesConfiguration.setHost(host);
        instancesConfiguration.setRecordVersion(recordVersion);
        instancesConfiguration.setDataUpdateTime(new Date());
        super.insert(instancesConfiguration);
    }

}