package com.arextest.report.model.api.contracts.label;

import lombok.Data;

/**
 * @author b_yu
 * @since 2022/11/22
 */
@Data
public class LabelType {
    private String id;
    private String labelName;
    private String color;
    private String workspaceId;
}
