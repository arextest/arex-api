package com.arextest.web.model.dto.iosummary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.arextest.web.model.dto.BaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SceneInfo extends BaseDto {
    private int code;
    private int count;
    private long categoryKey;
    private String planId;
    private String planItemId;
    private List<SubSceneInfo> subScenes;
    private Map<String, SubSceneInfo> subSceneInfoMap;
    private boolean reCalculated;
    SceneInfo(int code, long categoryKey, String planId, String planItemId, Map<String, SubSceneInfo> subSceneInfoMap) {
        this.code = code;
        this.categoryKey = categoryKey;
        this.planId = planId;
        this.planItemId = planItemId;
        this.subSceneInfoMap = subSceneInfoMap;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int code;
        private long categoryKey;
        private String planId;
        private String planItemId;
        private Map<String, SubSceneInfo> subSceneMap;

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder categoryKey(long categoryKey) {
            this.categoryKey = categoryKey;
            return this;
        }

        public Builder planId(String planId) {
            this.planId = planId;
            return this;
        }

        public Builder planItemId(String planItemId) {
            this.planItemId = planItemId;
            return this;
        }

        public Builder summary(CaseSummary summary) {
            if (CollectionUtils.isNotEmpty(summary.getDiffs())) {
                SubSceneInfo subSceneInfo = new SubSceneInfo(summary.getCode(), summary.getRecordId(),
                    summary.getReplayId(), summary.getDiffs());
                subSceneMap = new HashMap<>();
                subSceneMap.put(String.valueOf(summary.groupKey()), subSceneInfo);
            }
            return this;
        }

        public SceneInfo build() {
            return new SceneInfo(code, categoryKey, planId, planItemId, subSceneMap == null ? null : subSceneMap);
        }
    }
}
