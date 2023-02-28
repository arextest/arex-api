package com.arextest.web.model.dto.iosummary;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubSceneInfo {

    private int code;
    private int count;

    private final String recordId;
    private final String replayId;

    private List<DiffDetail> details;

    public SubSceneInfo(String recordId, String replayId, int code, List<DiffDetail> details) {
        this.code = code;
        this.details = details;
        this.recordId = recordId;
        this.replayId = replayId;
    }

    public void increment() {
        this.count++;
    }
}
