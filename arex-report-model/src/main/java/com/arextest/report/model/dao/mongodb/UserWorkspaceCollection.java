package com.arextest.report.model.dao.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "UserWorkspace")
public class UserWorkspaceCollection extends ModelBase {
    private String userName;
    private String workspaceId;
    private Integer role;
    private Integer status;
    private String token;
}
