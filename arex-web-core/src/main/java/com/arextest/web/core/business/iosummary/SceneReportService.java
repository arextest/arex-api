package com.arextest.report.core.business.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SceneReportService {

    /**
     * 差异类型场景分组
     */
    public List<SceneInfo> report(String planItemId) {
        List<CaseSummary> data = new ArrayList<>(); // todo: query from mongo
        if (CollectionUtil.isEmpty(data)) {
            return null;
        }

        return groupMainScene(data).stream()
                .map(b -> b.build())
                .sorted(Comparator.comparingInt(SceneInfo::getCode))
                .collect(Collectors.toList());
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
