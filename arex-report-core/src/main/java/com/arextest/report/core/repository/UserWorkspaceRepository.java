package com.arextest.report.core.repository;


import com.arextest.report.model.dto.filesystem.UserWorkspaceDto;

import java.util.List;

public interface UserWorkspaceRepository extends RepositoryProvider {
    UserWorkspaceDto queryUserWorkspace(String userName, String workspaceId);

    UserWorkspaceDto update(UserWorkspaceDto dto);

    Boolean verify(UserWorkspaceDto dto);

    List<UserWorkspaceDto> queryWorkspacesByUser(String userName);

    Boolean remove(String userName, String workspaceId);

    Boolean removeByWorkspaceId(String workspaceId);
}
