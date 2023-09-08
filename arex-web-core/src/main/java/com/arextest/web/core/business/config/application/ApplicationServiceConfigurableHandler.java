package com.arextest.web.core.business.config.application;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arextest.config.model.dto.StatusType;
import com.arextest.config.model.dto.application.ApplicationOperationConfiguration;
import com.arextest.config.model.dto.application.ApplicationServiceConfiguration;
import com.arextest.config.model.dto.application.OperationDescription;
import com.arextest.config.model.dto.application.ServiceDescription;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LogUtils;
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
