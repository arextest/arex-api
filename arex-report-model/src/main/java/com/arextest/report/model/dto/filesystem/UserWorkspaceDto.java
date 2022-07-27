package com.arextest.report.model.dto.filesystem;

import lombok.Data;

@Data
public class UserWorkspaceDto {
    private String email;
    private String workspaceId;
    private Integer role;
    private Integer status;
    private String token;
}
