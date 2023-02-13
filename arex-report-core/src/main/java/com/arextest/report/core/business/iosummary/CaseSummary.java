package com.arextest.report.core.business.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class CaseSummary {
    static final int OFFSET_BASIS = 0x811C9DC5; //2166136261
    static final int FNV_PRIME = 16777619;

    public static Builder builder() {
        return new Builder();
    }

    private int code;
    private String recordId;
    private String replayId;
    private String planId;
    private String planItemId;

    private List<DiffDetail> diffs;

    CaseSummary(String planId, String planItemId, String recordId, String replayId, int code, List<DiffDetail> diffs) {
        this.planId = planId;
        this.planItemId = planItemId;
        this.recordId = recordId;
        this.replayId = replayId;
        this.code = code;
        this.diffs = diffs;
    }

    public long groupKey() {
        if (CollectionUtil.isEmpty(diffs)) {
            return 0;
        }

        long key = OFFSET_BASIS;
        for (DiffDetail detail : diffs) {
            key = (key ^ detail.code) * FNV_PRIME;
            byte[] data = detail.categoryName.getBytes();
            for (int i = 0; i < 5 && i < data.length; i++) {
                key = (key ^ data[i]) * FNV_PRIME;
            }

            for (byte c : detail.operationName.getBytes()) {
                key = (key ^ c) * FNV_PRIME;
            }
        }

        key += key << 13;
        key ^= key >> 7;
        key += key << 3;
        key ^= key >> 17;
        key += key << 5;
        return Math.abs(key);
    }

    static class Builder {
        private String recordId;
        private String replayId;
        private String planId;
        private String planItemId;

        private List<DiffDetail> diffs;

        /**
         * -1 : exception
         * 0: success
         * 1: value diff
         * 2: left call missing
         * 4: right call missing
         */
        private int code;

        public Builder recordId(String recordId) {
            this.recordId = recordId;
            return this;
        }

        public Builder rePlayId(String replayId) {
            this.replayId = replayId;
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

        public Builder detail(String categoryName, String operationName, UnmatchedCategory unmatched) {
            if (code < 0) {
                return this;
            }

            if (diffs == null) {
                diffs = new ArrayList<>();
            }

            if (!exists(categoryName, operationName, unmatched)) {
                code = code | unmatched.getCode();
                diffs.add(new DiffDetail(unmatched.getCode(), categoryName, operationName));
            }
            return this;
        }

        private boolean exists(String categoryName, String operationName, UnmatchedCategory unmatched) {
            for (DiffDetail detail : diffs) {
                if (detail.code == unmatched.getCode() && detail.categoryName == categoryName
                        && detail.operationName == operationName) {
                    return true;
                }
            }
            return false;
        }

        public Builder success() {
            this.code = 0;
            return this;
        }

        public Builder failed() {
            this.code = -1;
            return this;
        }

        public CaseSummary build() {
            return code <= 0 ? new CaseSummary(planId, planItemId, recordId, replayId, code, null)
                    : new CaseSummary(planId, planItemId, recordId, replayId, code, diffs);
        }
    }
}
