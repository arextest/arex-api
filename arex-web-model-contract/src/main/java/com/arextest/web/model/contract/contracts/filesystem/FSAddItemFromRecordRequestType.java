package com.arextest.web.model.contract.contracts.filesystem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FSAddItemFromRecordRequestType {
    @NotBlank(message = "WorkspaceId cannot be empty")
    private String workspaceId;
    private String[] parentPath;
    private String nodeName;
    @NotBlank(message = "planId cannot be empty")
    private String planId;
    @NotBlank(message = "RecordId cannot be empty")
    private String recordId;
    @JsonIgnore
    private String userName;
    @NotBlank(message = "operationId cannot be empty")
    private String operationId;
}
