package com.arextest.report.web.api.service.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;

@Configuration
public class SpringConvertors {

    @Bean
    public ConversionService conversionService(String2Map linkedHashMap2String) {
        ConversionServiceFactoryBean conversionServiceFactory = new ConversionServiceFactoryBean();
        conversionServiceFactory.setConverters(Collections.singleton(linkedHashMap2String));
        conversionServiceFactory.afterPropertiesSet();
        return conversionServiceFactory.getObject();
    }
}