package com.arextest.report;

import com.arextest.report.core.business.configservice.ConfigService;
import com.arextest.report.core.business.configservice.handler.ConfigurableHandler;
import com.arextest.report.core.repository.mongo.DynamicClassConfigurationRepositoryImpl;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.record.DynamicClassConfiguration;
import com.arextest.report.web.api.service.WebSpringBootServletInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by rchen9 on 2022/9/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {WebSpringBootServletInitializer.class})
public class ConfigServiceTest {

    @Autowired
    ConfigService configService;

    @Resource
    DynamicClassConfigurationRepositoryImpl dynamicClassConfigurationRepository;

    @Test
    public void testConfig() {
        QueryConfigTemplateRequestType queryConfigTemplateRequestType = new QueryConfigTemplateRequestType();
        queryConfigTemplateRequestType.setAppId("arex.1.20220909A");
        QueryConfigTemplateResponseType queryConfigTemplateResponseType = configService.queryConfigTemplate(queryConfigTemplateRequestType);
        System.out.println();
    }

    @Test
    public void testDynamic(){
        boolean b = dynamicClassConfigurationRepository.removeByAppId("222");
        System.out.println();
    }
}
