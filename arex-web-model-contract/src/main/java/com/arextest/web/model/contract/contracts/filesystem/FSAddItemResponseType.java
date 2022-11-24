package com.arextest.web.model.contract.contracts.filesystem;

import lombok.Data;

@Data
public class FSAddItemResponseType {
    private Boolean success;
    private String infoId;
    private String workspaceId;
}
