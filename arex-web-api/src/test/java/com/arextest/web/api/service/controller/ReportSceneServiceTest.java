package com.arextest.web.api.service.controller;

import com.arextest.web.api.service.WebSpringBootServletInitializer;
import com.arextest.web.core.business.iosummary.SceneReportService;
import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import com.arextest.web.model.dto.iosummary.DiffDetail;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import com.arextest.web.model.dto.iosummary.SubSceneInfo;
import com.google.common.collect.ImmutableMap;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

/**
 * Created by rchen9 on 2023/6/9.
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebSpringBootServletInitializer.class})
public class ReportSceneServiceTest {

    @Autowired
    SceneInfoRepository sceneInfoRepository;

    @Autowired
    SceneReportService sceneReportService;

    @Autowired
    CaseSummaryRepository caseSummaryRepository;

    @Test
    public void testSceneSave() {

        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setCode(1);
        sceneInfo.setCount(1);
        sceneInfo.setPlanId("111");
        sceneInfo.setPlanItemId("1111");

        SubSceneInfo subSceneInfo = new SubSceneInfo();
        subSceneInfo.setCode(1);
        subSceneInfo.setCount(1);
        DiffDetail diffDetail = new DiffDetail();
        diffDetail.setCategoryName("Servlet");
        diffDetail.setCode(1);
        diffDetail.setOperationName("/dynamicTest/testRandomInt");
        subSceneInfo.setDetails(Collections.singletonList(diffDetail));
        subSceneInfo.setRecordId("AREX-172-27-0-4-7317404723087");
        subSceneInfo.setReplayId("AREX-172-18-0-4-751739346794320");
        sceneInfo.setSubSceneInfoMap(ImmutableMap.of("1", subSceneInfo));
        sceneInfoRepository.save(sceneInfo);

    }

    @Test
    public void testReport() {
        String planId = "6482ebe22238060b941c903d";
        String planItemId = "6482ebe22238060b941c904f";
        List<CaseSummary> caseSummaryList = caseSummaryRepository.query(planId, planItemId);
        caseSummaryList.forEach(item -> sceneReportService.report(item));
    }

}
