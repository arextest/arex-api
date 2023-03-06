package com.arextest.web.model.dto.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SceneInfo {
    public static Builder builder() {
        return new Builder();
    }

    private int code;
    private int count;
    private String planId;
    private String planItmId;
    private List<SubSceneInfo> subScenes;

    SceneInfo(int code, int count, String planId, String planItmId, List<SubSceneInfo> subScenes) {
        this.code = code;
        this.count = count;
        this.planId = planId;
        this.planItmId = planItmId;
        this.subScenes = subScenes;
    }

    public static class Builder {
        private int code;
        private int count;
        private String planId;
        private String planItmId;
        private Map<Long, SubSceneInfo> subSceneMap;

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder planId(String planId) {
            this.planId = planId;
            return this;
        }

        public Builder planItemId(String planItemId) {
            this.planItmId = planItemId;
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
            return new SceneInfo(code, count, planId, planItmId,
                    subSceneMap == null ? null : new ArrayList<>(subSceneMap.values()));
        }
    }
}
