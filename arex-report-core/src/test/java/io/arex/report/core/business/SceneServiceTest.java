package io.arex.report.core.business;

import io.arex.report.core.repository.ReportDiffAggStatisticRepository;
import io.arex.report.core.repository.ReportPlanStatisticRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SceneService.class)
@MockBean({ReportDiffAggStatisticRepository.class, ReportPlanStatisticRepository.class})
public class SceneServiceTest {

    @Test
    public void testSubList() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        List<Integer> subList = list.subList(0, list.size());
        assertEquals(1, subList.size());
    }
}