package com.arextest.web.core.business.config.application;

import com.arextest.web.core.business.config.AbstractConfigurableHandler;
import com.arextest.web.core.repository.ConfigRepositoryProvider;
import com.arextest.web.model.contract.contracts.config.application.ApplicationConfiguration;
import com.arextest.web.model.contract.contracts.config.application.ApplicationDescription;
import com.arextest.web.model.contract.contracts.config.application.provider.ApplicationDescriptionProvider;
import com.arextest.web.model.contract.contracts.common.enums.StatusType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author jmo
 * @since 2022/1/23
 */
@Component
class ApplicationConfigurableHandler extends AbstractConfigurableHandler<ApplicationConfiguration> {
    @Resource
    private ApplicationDescriptionProvider applicationDescriptionProvider;

    protected ApplicationConfigurableHandler(@Autowired ConfigRepositoryProvider<ApplicationConfiguration> repositoryProvider) {
        super(repositoryProvider);
    }

    @Override
    public boolean insert(ApplicationConfiguration configuration) {
        if (configuration == null || StringUtils.isEmpty(configuration.getAppId())) {
            return false;
        }
        ApplicationDescription applicationOrganization = applicationDescriptionProvider.get(configuration.getAppId());
        if (applicationOrganization != null) {
            configuration.setAppName(applicationOrganization.getAppName());
            configuration.setDescription(applicationOrganization.getDescription());
            configuration.setGroupId(applicationOrganization.getGroupId());
            configuration.setGroupName(applicationOrganization.getGroupName());
            configuration.setOrganizationId(applicationOrganization.getOrganizationId());
            configuration.setOrganizationName(applicationOrganization.getOrganizationName());
            configuration.setOwner(applicationOrganization.getOwner());
            configuration.setCategory(applicationOrganization.getCategory());
        }
        return super.insert(configuration);
    }

    @Override
    protected List<ApplicationConfiguration> createFromGlobalDefault(String appId) {
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
        applicationConfiguration.setAppId(appId);
        applicationConfiguration.setAgentVersion(StringUtils.EMPTY);
        applicationConfiguration.setAgentExtVersion(StringUtils.EMPTY);
        applicationConfiguration.setRecordedCaseCount(0);
        applicationConfiguration.setStatus(StatusType.RECORD.getMask() | StatusType.REPLAY.getMask());
        this.insert(applicationConfiguration);
        return Collections.singletonList(applicationConfiguration);
    }
}
