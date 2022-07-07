package com.arextest.report.web.api.service.beans;

import com.arextest.report.core.business.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class PropertiesConfigration {
    @Value("${configService.url}")
    private String configServiceUrl;

    @Value("${emailHost}")
    private String emailHost;

    @Value("${emailFrom}")
    private String emailFrom;

    @Value("${emailPwd}")
    private String emailPwd;


    @Bean
    public ApplicationProperties addProperties() {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        applicationProperties.setConfigServiceUrl(configServiceUrl);
        applicationProperties.setEmailHost(emailHost);
        applicationProperties.setEmailFrom(emailFrom);
        applicationProperties.setEmailPwd(emailPwd);
        return applicationProperties;
    }
}
