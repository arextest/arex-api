package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class FSAddItemFromRecordResponseType {
    private Boolean success;
    private String infoId;
    private String workspaceId;
}
