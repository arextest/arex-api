package com.arextest.web.model.dto.filesystem;

import lombok.Data;

@Data
public class UserWorkspaceDto {
    private String userName;
    private String workspaceId;
    private Integer role;
    private Integer status;
    private String token;
}
