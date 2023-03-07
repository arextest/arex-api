package com.arextest.web.api.service.controller;

import com.arextest.web.api.service.WebSpringBootServletInitializer;
import com.arextest.web.core.business.iosummary.SceneReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by rchen9 on 2023/3/7.
 */
@SpringBootTest(classes = WebSpringBootServletInitializer.class)
@RunWith(SpringRunner.class)
public class SceneReportServiceTest {

    @Autowired
    private SceneReportService sceneReportService;

    @Test
    public void testReport() {
        String planId = "6406f9fe78b64d7f552679c9";
        String planItemId = "6406f9fe78b64d7f552679f7";
        sceneReportService.report(planId, planItemId);

    }
}
