// package com.arextest.report.core.business;
//
// import com.arextest.report.core.repository.ReportPlanStatisticRepository;
// import com.arextest.report.core.repository.mongo.ReportPlanItemStatisticRepositoryImpl;
// import com.arextest.report.model.dto.CompareResultDto;
// import com.arextest.report.model.enums.DiffResultCode;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.junit4.SpringRunner;
//
// import javax.annotation.Resource;
// import java.util.ArrayList;
// import java.util.List;
//
// import static org.junit.Assert.assertEquals;
//
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = StatisticService.class)
// @MockBean({ReportPlanItemStatisticRepositoryImpl.class, ReportPlanStatisticRepository.class})
// public class StatisticServiceTest {
//
//     @Resource
//     private StatisticService service;
//
//     @Test
//     public void statisticPlanItems() {
//         List<CompareResultDto> results = new ArrayList<>();
//         for (int i = 0; i < 100; i++) {
//             CompareResultDto r = new CompareResultDto();
//             r.setPlanItemId(Long.valueOf(i % 5));
//             r.setReplayId("replayId" + (i % 10));
//
//             if (i % 3 == 0) {
//                 r.setDiffResultCode(DiffResultCode.COMPARED_WITHOUT_DIFFERENCE);
//
//             } else if (i % 3 == 1) {
//                 r.setDiffResultCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);
//             } else {
//                 r.setDiffResultCode(DiffResultCode.COMPARED_INTERNAL_EXCEPTION);
//             }
//             results.add(r);
//         }
//         service.statisticPlanItems(results);
//         assertEquals(5, service.getPlanItemMap().size());
//     }
// }
