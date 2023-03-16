package com.arextest.web.core.business.config.application;

import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.model.contract.contracts.config.application.InstancesConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Slf4j
@Component
public final class ApplicationInstancesConfigurableHandler extends AbstractConfigurableHandler<InstancesConfiguration> {
    public static final Integer TEN_MINIT_MILLS = 10 * 60 * 1000;

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
                super.update(instancesConfiguration);
            } else {
                create(appId, host, recordVersion);
            }
        }
        deleteExpireInstance(instancesConfigurations);
    }

    private void create(String appId, String host, String recordVersion) {
        InstancesConfiguration instancesConfiguration = new InstancesConfiguration();
        instancesConfiguration.setAppId(appId);
        instancesConfiguration.setHost(host);
        instancesConfiguration.setRecordVersion(recordVersion);
        super.insert(instancesConfiguration);
    }

    private void deleteExpireInstance(List<InstancesConfiguration> instancesConfigurations) {
        long expireTime = System.currentTimeMillis() - TEN_MINIT_MILLS;
        List<InstancesConfiguration> expireInstances =
                instancesConfigurations.stream().filter(instancesConfiguration -> instancesConfiguration.getModifiedTime().before(new Timestamp(expireTime))).collect(Collectors.toList());
        super.removeList(expireInstances);
    }

}