package com.arextest.report.model.api.contracts.label;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author b_yu
 * @since 2022/11/22
 */
@Data
public class QueryLabelsByWorkspaceIdRequestType {
    @NotBlank(message = "Workspace id cannot be empty")
    private String workspaceId;
}
