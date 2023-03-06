package com.arextest.web.api.service.controller;

import com.arextest.web.api.service.WebSpringBootServletInitializer;
import com.arextest.web.core.business.iosummary.SceneReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by rchen9 on 2023/2/9.
 */
@SpringBootTest(classes = WebSpringBootServletInitializer.class)
@RunWith(SpringRunner.class)
public class SceneReportServiceTest {

    @Autowired
    SceneReportService sceneReportService;


    @Test
    public void testUpdate() {
        String planItemId = "640579fd297ce550ed39d140";
        sceneReportService.report(planItemId);
    }
}
