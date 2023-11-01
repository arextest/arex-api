package com.arextest.web.model.dto.iosummary;

import cn.hutool.core.collection.CollectionUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.MutablePair;

@Data
@NoArgsConstructor
public class CaseSummary {

  static final int OFFSET_BASIS = 0x811C9DC5; // 2166136261
  static final int FNV_PRIME = 16777619;
  private int code;
  private String recordId;
  private String replayId;
  private String planId;
  private String planItemId;
  private List<DiffDetail> diffs;
  private Long categoryKey;
  private Long groupKey;

  CaseSummary(String planId, String planItemId, String recordId, String replayId, int code,
      List<DiffDetail> diffs) {
    this.planId = planId;
    this.planItemId = planItemId;
    this.recordId = recordId;
    this.replayId = replayId;
    this.code = code;
    this.diffs = diffs;
  }

  public static Builder builder() {
    return new Builder();
  }

  public long categoryKey() {
    if (this.categoryKey != null) {
      return this.categoryKey;
    }

    if (CollectionUtil.isEmpty(diffs)) {
      this.categoryKey = 0L;
    } else {
      Set<MutablePair<String, Integer>> categoryAndCodeSet = new HashSet<>();
      for (DiffDetail diffDetail : diffs) {
        categoryAndCodeSet.add(new MutablePair<>(diffDetail.categoryName, diffDetail.code));
      }

      long key = OFFSET_BASIS;
      for (MutablePair<String, Integer> item : categoryAndCodeSet) {
        key = (key ^ item.getRight()) * FNV_PRIME;
        for (byte c : item.getLeft().getBytes()) {
          key = (key ^ c) * FNV_PRIME;
        }
      }
      key += key << 13;
      key ^= key >> 7;
      key += key << 3;
      key ^= key >> 17;
      key += key << 5;
      this.categoryKey = Math.abs(key);
    }
    return this.categoryKey;
  }

  public long groupKey() {
    if (this.groupKey != null) {
      return this.groupKey;
    }

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

  public static class Builder {

    private String recordId;
    private String replayId;
    private String planId;
    private String planItemId;

    private List<DiffDetail> diffs;

    /**
     * -1 : exception 0: success 1: value diff 2: left call missing 4: right call missing
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
        if (detail.code == unmatched.getCode() && Objects.equals(detail.categoryName, categoryName)
            && Objects.equals(detail.operationName, operationName)) {
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
