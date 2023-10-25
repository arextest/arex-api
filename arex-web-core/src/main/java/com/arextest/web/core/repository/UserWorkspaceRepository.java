package com.arextest.web.core.repository;

import java.util.List;

import com.arextest.web.model.dto.filesystem.UserWorkspaceDto;

public interface UserWorkspaceRepository extends RepositoryProvider {
    UserWorkspaceDto queryUserWorkspace(String userName, String workspaceId);

    UserWorkspaceDto update(UserWorkspaceDto dto);

    Boolean verify(UserWorkspaceDto dto);

    List<UserWorkspaceDto> queryWorkspacesByUser(String userName);

    List<UserWorkspaceDto> queryUsersByWorkspace(String workspaceId);

    Boolean remove(String userName, String workspaceId);

    Boolean removeByWorkspaceId(String workspaceId);
}
