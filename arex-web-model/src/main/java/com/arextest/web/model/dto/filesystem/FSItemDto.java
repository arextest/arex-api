package com.arextest.web.model.dto.filesystem;

import lombok.Data;

import java.util.Set;

@Data
public class FSItemDto {
    private String id;
    private String workspaceId;
    private String parentId;
    private Integer parentNodeType;
    private Set<String> labelIds;
}
