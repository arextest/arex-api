package io.arex.report.model.api.contracts.common;

import lombok.Data;


@Data
public class Difference {
    private String differenceName;
    private Integer sceneCount;
    private Integer caseCount;
}
