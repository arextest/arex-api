package com.arextest.report;

import com.arextest.report.core.business.configservice.QueryYamlTemplateService;
import com.arextest.report.core.business.configservice.UpdateYamlTemplateService;
import com.arextest.report.core.repository.mongo.ApplicationOperationConfigurationRepositoryImpl;
import com.arextest.report.core.repository.mongo.DynamicClassConfigurationRepositoryImpl;
import com.arextest.report.model.api.contracts.configservice.PushYamlTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.QueryYamlTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.QueryYamlTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.application.ApplicationOperationConfiguration;
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
    QueryYamlTemplateService queryYamlTemplateService;

    @Autowired
    UpdateYamlTemplateService updateYamlTemplateService;

    @Resource
    DynamicClassConfigurationRepositoryImpl dynamicClassConfigurationRepository;

    @Resource
    ApplicationOperationConfigurationRepositoryImpl applicationOperationConfigurationRepository;

    @Test
    public void testConfig() {
        QueryYamlTemplateRequestType queryYamlTemplateRequestType = new QueryYamlTemplateRequestType();
        queryYamlTemplateRequestType.setAppId("arex.1.20220909A");
        QueryYamlTemplateResponseType queryYamlTemplateResponseType = queryYamlTemplateService.queryConfigTemplate(queryYamlTemplateRequestType);
        System.out.println();
    }

    @Test
    public void testDynamic() {
        boolean b = dynamicClassConfigurationRepository.removeByAppId("222");
        System.out.println();
    }

    @Test
    public void testApplicationOperationConfigurationUpdate() {
        ApplicationOperationConfiguration applicationOperationConfiguration = new ApplicationOperationConfiguration();
        applicationOperationConfiguration.setId("631ae5ccf3d7bbb3281e2f39");
        applicationOperationConfiguration.setOperationResponse("{\"a\":\"b\"}");
        boolean update = applicationOperationConfigurationRepository.update(applicationOperationConfiguration);
        System.out.println();
    }

    @Test
    public void testUpdateConfig() {
        String template = "compareConfig:\n" +
                "- exclusions:\n" +
                "  - test3/utest4\n" +
                "  inclusions:\n" +
                "  - 2test3/u2test4\n" +
                "  listSort:\n" +
                "  - keys:\n" +
                "    - '111'\n" +
                "    - 111/u2222\n" +
                "    listPath: test3/utest4\n" +
                "  operationName: /owners/{ownerId}/edit\n" +
                "  references:\n" +
                "  - fkPath: g111/u2222\n" +
                "    pkPath: test3/utest4\n" +
                "- exclusions: null\n" +
                "  inclusions: null\n" +
                "  listSort:\n" +
                "  - keys:\n" +
                "    - '111'\n" +
                "    - 111/u2222\n" +
                "    listPath: gtest3/utest4\n" +
                "  operationName: null\n" +
                "  references:\n" +
                "  - fkPath: 111/u2222\n" +
                "    pkPath: gtest3/utest4\n" +
                "recordConfig:\n" +
                "  dynamicClass:\n" +
                "  - fullClassName: test\n" +
                "  - fullClassName: '1'\n" +
                "  - fullClassName: ddd\n" +
                "  - fullClassName: ttt\n" +
                "  - fullClassName: java.lang.String\n" +
                "  serviceConfig:\n" +
                "    allowDayOfWeeks: 2\n" +
                "    allowTimeOfDayFrom: 00:01\n" +
                "    allowTimeOfDayTo: '23:59'\n" +
                "    excludeDependentOperationSet: []\n" +
                "    excludeDependentServiceSet: []\n" +
                "    excludeOperationSet:\n" +
                "    - /error\n" +
                "    - checkHealth\n" +
                "    includeOperationSet: []\n" +
                "    includeServiceSet: []\n" +
                "    sampleRate: 1\n" +
                "replayConfig:\n" +
                "  offsetDays: 20";
        PushYamlTemplateRequestType pushYamlTemplateRequestType = new PushYamlTemplateRequestType();
        pushYamlTemplateRequestType.setAppId("arex.1.20220909A");
        pushYamlTemplateRequestType.setConfigTemplate(template);
        updateYamlTemplateService.pushConfigTemplate(pushYamlTemplateRequestType);
        System.out.println();

    }
}
