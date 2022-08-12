package com.arextest.report.model.api.contracts.filesystem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class FSAddItemFromRecordRequestType {
    private String workspaceId;
    private String[] parentPath;
    private String nodeName;
    private String recordId;
    @JsonIgnore
    private String userName;
}
