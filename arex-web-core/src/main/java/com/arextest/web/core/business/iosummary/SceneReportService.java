package com.arextest.web.core.business.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import com.arextest.web.core.repository.CaseSummaryRepository;
import com.arextest.web.core.repository.SceneInfoRepository;
import com.arextest.web.model.dto.iosummary.CaseSummary;
import com.arextest.web.model.dto.iosummary.SceneInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SceneReportService {

    @Autowired
    CaseSummaryRepository caseSummaryRepository;

    @Autowired
    SceneInfoRepository sceneInfoRepository;

    /**
     * 差异类型场景分组
     */
    public void report(String planItemId) {
        List<CaseSummary> data = caseSummaryRepository.query(planItemId);
        if (CollectionUtil.isEmpty(data)) {
            return;
        }
        List<SceneInfo> sceneInfos = groupMainScene(data).stream()
                .map(SceneInfo.Builder::build)
                .sorted(Comparator.comparingInt(SceneInfo::getCode))
                .collect(Collectors.toList());
        sceneInfoRepository.save(sceneInfos);
    }

    /**
     * 根据Code分组主场景
     */
    private Collection<SceneInfo.Builder> groupMainScene(List<CaseSummary> caseSummaries) {
        Map<Integer, SceneInfo.Builder> main = new HashMap<>();
        // group性能不佳，自己分组
        for (CaseSummary summary : caseSummaries) {
            main.computeIfAbsent(summary.getCode(), k -> SceneInfo.builder()
                    .code(summary.getCode()))
                    .summary(summary);
        }
        return main.values();
    }
}
