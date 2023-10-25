package com.arextest.web.model.dao.mongodb;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "FSFolder")
public class FSFolderCollection extends ModelBase {
    private String name;
    private String workspaceId;
    private String parentId;
    private Integer parentNodeType;
    private Map<String, Object> customTags;
}
