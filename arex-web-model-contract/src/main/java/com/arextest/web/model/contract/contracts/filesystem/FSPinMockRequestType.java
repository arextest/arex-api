package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author b_yu
 * @since 2022/12/15
 */
@Data
public class FSPinMockRequestType {
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    @NotBlank(message = "Item InfoId cannot be empty")
    private String infoId;
    private int nodeType;
    @NotBlank(message = "RecordId cannot be empty")
    private String recordId;
}
