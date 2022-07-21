package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

@Data
public class FSMoveItemRequestType {
    private String id;
    private String[] fromNodePath;
    private String[] toParentPath;
}
