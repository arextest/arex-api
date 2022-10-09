package com.arextest.report.model.api.contracts.filesystem;

import lombok.Data;

/**
 * @author b_yu
 * @since 2022/10/8
 */
@Data
public class FSImportItemRequestType {
    private String workspaceId;
    private String[] path;
    private int type;
    private String importString;
}
