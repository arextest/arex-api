// package com.arextest.report.model.mapper;
//
// import com.arextest.report.model.dao.mongodb.ReportDiffAggStatisticCollection;
// import com.arextest.report.model.dao.mongodb.entity.SceneDetail;
// import com.arextest.report.model.dto.DiffAggDto;
// import com.arextest.report.model.dto.SceneDetailDto;
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
// @SpringBootTest(classes = DiffAggMapper.class)
// @RunWith(SpringRunner.class)
// public class DiffAggMapperTest {
//
// @Test
// public void testDaoFromDto() {
// DiffAggDto dto = new DiffAggDto();
// dto.setPlanItemId(1L);
// dto.setPlanId(2L);
// SceneDetailDto detailDto = new SceneDetailDto();
// detailDto.setLogIndexes("1_2");
// detailDto.setCompareResultId("abc");
// Map<String, Map<String, SceneDetailDto>> diffSceneMap = new HashMap<>();
// Map<String, SceneDetailDto> sceneMap = new HashMap<>();
// sceneMap.put("scene", detailDto);
// diffSceneMap.put("fuzzyPath", sceneMap);
// dto.setDifferences(diffSceneMap);
//
// ReportDiffAggStatisticCollection dao = DiffAggMapper.INSTANCE.daoFromDto(dto);
// assertNotNull(dao);
// }
//
// @Test
// public void testDtoFromDao() {
// ReportDiffAggStatisticCollection dao = new ReportDiffAggStatisticCollection();
// dao.setPlanItemId(1L);
// dao.setPlanId(2L);
// SceneDetail detail = new SceneDetail();
// detail.setLogIndexes("1_2");
// detail.setCompareResultId("abc");
// Map<String, Map<String, SceneDetail>> diffSceneMap = new HashMap<>();
// Map<String, SceneDetail> sceneMap = new HashMap<>();
// sceneMap.put("scene", detail);
// diffSceneMap.put("fuzzyPath", sceneMap);
// dao.setDifferences(diffSceneMap);
//
// DiffAggDto dto = DiffAggMapper.INSTANCE.dtoFromDao(dao);
// assertNotNull(dto);
// }
// }
