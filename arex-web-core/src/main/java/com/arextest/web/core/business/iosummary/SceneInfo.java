package com.arextest.report.core.business.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SceneInfo {
    public static Builder builder() {
        return new Builder();
    }

    private final int code;
    private final int count;
    private final List<SubSceneInfo> subScenes;

    SceneInfo(int code, int count, List<SubSceneInfo> subScenes) {
        this.code = code;
        this.count = count;
        this.subScenes = subScenes;
    }

    static class Builder {
        private int code;
        private int count;
        private Map<Long, SubSceneInfo> subSceneMap;

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder summary(CaseSummary summary) {
            this.count++;
            if (!CollectionUtil.isEmpty(summary.getDiffs())) {
                if (subSceneMap == null) {
                    subSceneMap = new HashMap<>();
                }

                subSceneMap.computeIfAbsent(summary.groupKey(), k ->
                                new SubSceneInfo(summary.getRecordId(), summary.getReplayId(),
                                        summary.getCode(), summary.getDiffs()))
                        .increment();
            }

            return this;
        }

        public SceneInfo build() {
            return new SceneInfo(code, count,
                    subSceneMap == null ? null : new ArrayList<>(subSceneMap.values()));
        }
    }
}
