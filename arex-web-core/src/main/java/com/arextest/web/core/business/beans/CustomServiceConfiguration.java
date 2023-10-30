package com.arextest.web.core.business.beans;

import com.arextest.config.model.dto.application.ApplicationServiceConfiguration;
import com.arextest.web.core.business.ConfigLoadService;
import com.arextest.web.core.business.config.ConfigurableHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arextest.web.core.business.config.replay.*;
import com.arextest.web.core.repository.AppContractRepository;
import org.springframework.core.env.Environment;

@Configuration
public class CustomServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean(ComparisonSummaryService.class)
    public ComparisonSummaryService comparisonSummaryService(
        ComparisonExclusionsConfigurableHandler exclusionsConfigurableHandler,
        ComparisonInclusionsConfigurableHandler inclusionsConfigurableHandler,
        ComparisonEncryptionConfigurableHandler encryptionConfigurableHandler,
        ComparisonReferenceConfigurableHandler referenceConfigurableHandler,
        ComparisonListSortConfigurableHandler listSortConfigurableHandler,
        ConfigurableHandler<ApplicationServiceConfiguration> applicationServiceConfigurationConfigurableHandler,
        AppContractRepository appContractRepository,
        ComparisonIgnoreCategoryConfigurableHandler ignoreCategoryConfigurableHandler) {
        return new ComparisonSummaryService(exclusionsConfigurableHandler, inclusionsConfigurableHandler,
            encryptionConfigurableHandler, referenceConfigurableHandler, listSortConfigurableHandler,
            applicationServiceConfigurationConfigurableHandler, appContractRepository,
            ignoreCategoryConfigurableHandler);
    }

    @Bean
    @ConditionalOnMissingBean(ConfigLoadService.class)
    public ConfigLoadService configLoadService(Environment environment) {
        return new DefaultConfigLoadService(environment);
    }

}
