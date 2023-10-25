package com.arextest.web.core.business.beans;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arextest.config.model.dto.application.ApplicationServiceConfiguration;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonEncryptionConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonExclusionsConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonIgnoreCategoryConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonInclusionsConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonListSortConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonReferenceConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonSummaryService;
import com.arextest.web.core.repository.AppContractRepository;

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
}
