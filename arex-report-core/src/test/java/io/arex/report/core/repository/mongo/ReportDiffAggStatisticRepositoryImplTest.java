package io.arex.report.core.repository.mongo;

import io.arex.report.core.repository.ReportDiffAggStatisticRepository;
import io.arex.report.model.dto.DiffAggDto;
import io.arex.report.model.dto.SceneDetailDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReportDiffAggStatisticRepositoryImpl.class})
@MockBean(MongoTemplate.class)
public class ReportDiffAggStatisticRepositoryImplTest {
    @Autowired
    ReportDiffAggStatisticRepository repository;

    @Test
    public void updateDiffScenes() {
        DiffAggDto dto = new DiffAggDto();
        dto.setPlanId(1L);
        dto.setPlanItemId(2L);
        dto.setOperationId(3L);
        dto.setCategoryName("SOA");
        dto.setOperationName("testOperationName");
        Map<String, Map<String, SceneDetailDto>> diffScene = new HashMap<>();

        Map<String, SceneDetailDto> scene = new HashMap<>();
        SceneDetailDto detailDto = new SceneDetailDto();
        detailDto.setLogIndexes("1_2");
        detailDto.setCompareResultId("abc");
        scene.put("a_b", detailDto);
        diffScene.put("fuzzyPath1", scene);

        detailDto = new SceneDetailDto();
        detailDto.setLogIndexes("3_4");
        detailDto.setCompareResultId("abc");
        scene.put("a_c", detailDto);
        diffScene.put("fuzzyPath2", scene);

        dto.setDifferences(diffScene);

        Map<String, Integer> diffCaseCounts = new HashMap<>();
        diffCaseCounts.put("fuzzyPath1", 1);
        diffCaseCounts.put("fuzzyPath2", 1);
        dto.setDiffCaseCounts(diffCaseCounts);

        DiffAggDto newValue = repository.updateDiffScenes(dto);
    }
}
