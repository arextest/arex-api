package io.arex.report.web.api.service.beans;

import io.arex.report.core.business.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class PropertiesConfigration {
    @Value("${configService.url}")
    private String configServiceUrl;

    
    @Bean
    public ApplicationProperties addProperties() {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.setConfigServiceUrl(configServiceUrl);
        return applicationProperties;
    }
}
