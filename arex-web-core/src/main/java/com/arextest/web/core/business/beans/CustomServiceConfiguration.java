package com.arextest.web.core.business.beans;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.*;
import com.arextest.web.core.repository.AppContractRepository;
import com.arextest.web.model.contract.contracts.config.application.ApplicationServiceConfiguration;

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
        AppContractRepository appContractRepository) {
        return new ComparisonSummaryService(exclusionsConfigurableHandler, inclusionsConfigurableHandler,
            encryptionConfigurableHandler, referenceConfigurableHandler, listSortConfigurableHandler,
            applicationServiceConfigurationConfigurableHandler, appContractRepository);
    }
}
