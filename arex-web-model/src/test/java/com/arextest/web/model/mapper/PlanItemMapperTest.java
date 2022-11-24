// package com.arextest.report.model.mapper;
//
// import com.arextest.report.model.dao.mongodb.ReportPlanItemStatisticCollection;
// import com.arextest.report.model.dto.PlanItemDto;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.junit4.SpringRunner;
//
// import java.util.HashMap;
// import java.util.Map;
//
// import static org.junit.Assert.*;
//
// @SpringBootTest(classes = PlanItemMapper.class)
// @RunWith(SpringRunner.class)
// public class PlanItemMapperTest {
//
//     @Test
//     public void daoFromDto() {
//         PlanItemDto dto = new PlanItemDto();
//         dto.setPlanItemId(1L);
//         dto.setPlanId(2L);
//         Map<String, Integer> m = new HashMap<>();
//         m.put("aaa", 1);
//         dto.setCases(m);
//         ReportPlanItemStatisticCollection dao = PlanItemMapper.INSTANCE.daoFromDto(dto);
//         assertNotNull(dao.getCases());
//     }
//
//     @Test
//     public void dtoFromDao() {
//         ReportPlanItemStatisticCollection dao = new ReportPlanItemStatisticCollection();
//         dao.setPlanItemId(1L);
//         dao.setPlanId(2L);
//         Map<String, Integer> m = new HashMap<>();
//         m.put("aaa", 1);
//         dao.setCases(m);
//         PlanItemDto dto = PlanItemMapper.INSTANCE.dtoFromDao(dao);
//         assertNotNull(dto.getCases());
//     }
// }
