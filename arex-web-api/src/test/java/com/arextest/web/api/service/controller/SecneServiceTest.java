package com.arextest.web.api.service.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import com.arextest.web.core.business.SceneService;
import com.arextest.web.core.repository.ReportDiffAggStatisticRepository;
import com.arextest.web.model.contract.contracts.common.LogEntity;
import com.arextest.web.model.dao.mongodb.ReportDiffAggStatisticCollection;
import com.arextest.web.model.dto.CompareResultDto;
import com.arextest.web.model.dto.DifferenceDto;
import com.arextest.web.model.enums.DiffResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.DeleteResult;

/**
 * @author b_yu
 * @since 2023/4/23
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SecneServiceTest {

    @Resource
    private SceneService sceneService;

    @Resource
    private ReportDiffAggStatisticRepository repository;
    @Resource
    private MongoTemplate mongoTemplate;

    @Before
    public void before() {
        Query query = Query.query(Criteria.where("planItemId").is("planItemId").and("categoryName").is("categoryName")
            .and("operationName").is("operationName"));
        DeleteResult result = mongoTemplate.remove(query, ReportDiffAggStatisticCollection.class);
    }

    @Test
    public void testReport() throws Exception {
        for (int i = 0; i < 5; i++) {
            CountDownLatch latch = new CountDownLatch(1);
            Thread[] threads = new Thread[4];
            for (int j = 0; j < 4; j++) {
                threads[j] = new Thread(() -> {
                    try {
                        latch.await();
                        push100CompareResults();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                threads[j].start();
            }

            latch.countDown();
            Thread.sleep(6000);
        }

        List<DifferenceDto> differenceDtos = repository.queryDifferences("planItemId", "categoryName", "operationName");
        Assert.assertNotNull(differenceDtos);
        Assert.assertEquals(1, differenceDtos.size());
        Assert.assertEquals(differenceDtos.get(0).getCaseCount(), new Integer(2000));
    }

    private void push100CompareResults() throws Exception {
        List<CompareResultDto> results = new ArrayList<>();
        for (int i = 0; i < 100; i++) {

            ObjectMapper mapper = new ObjectMapper();
            LogEntity log = mapper.readValue("{\n" + "\t\"baseValue\": \"true\",\n" + "\t\"testValue\": \"false\",\n"
                + "\t\"logInfo\": \"The node value of [result] is different : {true} - {false}\",\n"
                + "\t\"pathPair\": {\n" + "\t\t\"unmatchedType\": 3,\n" + "\t\t\"leftUnmatchedPath\": [{\n"
                + "\t\t\t\"nodeName\": \"result\",\n" + "\t\t\t\"index\": 0\n" + "\t\t}],\n"
                + "\t\t\"rightUnmatchedPath\": [{\n" + "\t\t\t\"nodeName\": \"result\",\n" + "\t\t\t\"index\": 0\n"
                + "\t\t}],\n" + "\t\t\"listKeys\": [],\n" + "\t\t\"listKeyPath\": [],\n" + "\t\t\"trace\": {\n"
                + "\t\t\t\"currentTraceLeft\": null,\n" + "\t\t\t\"currentTraceRight\": null\n" + "\t\t}\n" + "\t},\n"
                + "\t\"addRefPkNodePathLeft\": null,\n" + "\t\"addRefPkNodePathRight\": null,\n" + "\t\"warn\": 0,\n"
                + "\t\"path\": \"result\",\n" + "\t\"logTag\": {\n" + "\t\t\"errorType\": 2\n" + "\t}\n" + "}",
                LogEntity.class);

            CompareResultDto r = new CompareResultDto();
            r.setPlanId("planId");
            r.setPlanItemId("planItemId");
            r.setCategoryName("categoryName");
            r.setOperationName("operationName");
            r.setRecordId("recordId" + i);
            r.setReplayId("replayId" + i);
            r.setLogs(Collections.singletonList(log));
            r.setDiffResultCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);
            results.add(r);
        }
        sceneService.statisticScenes(results);
    }
}
