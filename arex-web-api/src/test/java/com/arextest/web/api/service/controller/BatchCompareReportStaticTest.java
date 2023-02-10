package com.arextest.web.api.service.controller;

import com.arextest.web.api.service.WebSpringBootServletInitializer;
import com.arextest.web.core.repository.BatchCompareReportStatisticsRepository;
import com.arextest.web.model.dto.batchcomparereport.BatchCompareReportStatisticsDto;
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
public class BatchCompareReportStaticTest {

    @Autowired
    private BatchCompareReportStatisticsRepository batchCompareReportStatisticsRepository;


    @Test
    public void testUpdate() {
        BatchCompareReportStatisticsDto dto = new BatchCompareReportStatisticsDto();
        dto.setPlanId("1");
        dto.setInterfaceId("1");
        dto.setUnMatchedType(1);
        dto.setFuzzyPath("a");
        dto.setErrorCount(1);
        // dto.setIndexInCaseList(Arrays.asList(new IndexInCaseDto("1", 0)));
        boolean b = batchCompareReportStatisticsRepository.updateBatchCompareReportStatistics(dto);
        System.out.println();

    }
}
