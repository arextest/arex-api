package io.arex.report.model.api.contracts.common;

import lombok.Data;

import java.util.List;


@Data
public class Scene {
    private String sceneName;
    private String compareResultId;
    private String logIndexes;
}
