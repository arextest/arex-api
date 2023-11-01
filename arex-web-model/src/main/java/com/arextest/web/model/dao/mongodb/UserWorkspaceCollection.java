package com.arextest.web.model.dao.mongodb;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "UserWorkspace")
public class UserWorkspaceCollection extends ModelBase {
    private String userName;
    private String workspaceId;
    private Integer role;
    private Integer status;
    private String token;
}
