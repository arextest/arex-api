package com.arextest.report.model.dto.filesystem;

import lombok.Data;

@Data
public class FSFolderDto {
    private String id;
    private String workspaceId;
    private String parentId;
    private Integer parentNodeType;
}
