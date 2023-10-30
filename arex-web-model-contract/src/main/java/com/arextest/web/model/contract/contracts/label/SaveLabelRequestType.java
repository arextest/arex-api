package com.arextest.web.model.contract.contracts.label;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author b_yu
 * @since 2022/11/18
 */
@Data
public class SaveLabelRequestType {
    private String id;
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    @NotBlank(message = "Label name cannot be empty")
    private String labelName;
    private String color;
}
