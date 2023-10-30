package com.arextest.web.core.business.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.arextest.config.repository.impl.ApplicationConfigurationRepositoryImpl;
import com.arextest.config.repository.impl.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.config.repository.impl.ApplicationServiceConfigurationRepositoryImpl;
import com.arextest.config.repository.impl.DynamicClassConfigurationRepositoryImpl;
import com.arextest.config.repository.impl.InstancesConfigurationRepositoryImpl;
import com.arextest.config.repository.impl.ServiceCollectConfigurationRepositoryImpl;

import lombok.extern.slf4j.Slf4j;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class ConfigBeanAutoConfig {

    @Bean
    public ApplicationConfigurationRepositoryImpl applicationConfigurationRepositoryImpl(MongoTemplate mongoTemplate) {
        return new ApplicationConfigurationRepositoryImpl(mongoTemplate.getDb());
    }

    @Bean
    public ApplicationServiceConfigurationRepositoryImpl
        applicationServiceConfigurationRepositoryImpl(MongoTemplate mongoTemplate) {
        return new ApplicationServiceConfigurationRepositoryImpl(mongoTemplate.getDb());
    }

    @Bean
    public ApplicationOperationConfigurationRepositoryImpl
        applicationOperationConfigurationRepositoryImpl(MongoTemplate mongoTemplate) {
        return new ApplicationOperationConfigurationRepositoryImpl(mongoTemplate.getDb());
    }

    @Bean
    public InstancesConfigurationRepositoryImpl instancesConfigurationRepositoryImpl(MongoTemplate mongoTemplate) {
        return new InstancesConfigurationRepositoryImpl(mongoTemplate.getDb());
    }

    @Bean
    public ServiceCollectConfigurationRepositoryImpl
        serviceCollectConfigurationRepositoryImpl(MongoTemplate mongoTemplate) {
        return new ServiceCollectConfigurationRepositoryImpl(mongoTemplate.getDb());
    }

    @Bean
    public DynamicClassConfigurationRepositoryImpl
        dynamicClassConfigurationRepositoryImpl(MongoTemplate mongoTemplate) {
        return new DynamicClassConfigurationRepositoryImpl(mongoTemplate.getDb());
    }

}
