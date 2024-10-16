//package com.arextest.web.api.service.beans;
//
//import com.arextest.web.api.service.WebSpringBootServletInitializer;
//import com.arextest.web.core.business.iosummary.SceneReportService;
//import com.arextest.web.model.dto.iosummary.SceneInfo;
//import java.util.List;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.jupiter.api.Disabled;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@Disabled
//@SpringBootTest(classes = WebSpringBootServletInitializer.class)
//@RunWith(SpringRunner.class)
//@ActiveProfiles("dev")
//public class SceneReportServiceTest {
//
//  @Autowired
//  SceneReportService sceneReportService;
//
//  @Test
//  public void testQueryCompleteSceneInfo() {
//    //sceneReportService.queryCompleteSceneInfo();
//    String planId = "644a52398b7524356ab73792";
//    String planItemId = "644a52398b7524356ab73793";
//    List<SceneInfo> sceneInfos = sceneReportService.queryCompleteSceneInfo(planId, planItemId);
//    System.out.println(sceneInfos);
//  }
//
//
//}
