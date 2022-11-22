package com.arextest.report.model.api.contracts.label;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author b_yu
 * @since 2022/11/21
 */
@Data
public class RemoveLabelRequestType {
    @NotBlank(message = "Label Id cannot be empty")
    private String id;
    @NotBlank(message = "Workspace Id cannot be empty")
    private String workspaceId;
}
