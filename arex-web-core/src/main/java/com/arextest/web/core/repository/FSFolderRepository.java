package com.arextest.web.core.repository;

import java.util.List;
import java.util.Set;

import com.arextest.web.model.dto.filesystem.FSFolderDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;

public interface FSFolderRepository extends RepositoryProvider {
    String initFolder(String parentId, Integer parentNodeType, String workspaceId, String name);

    Boolean removeFolder(String id);

    Boolean removeFolders(Set<String> ids);

    FSFolderDto queryById(String id);

    String duplicate(FSFolderDto dto);

    List<FSItemDto> queryByIds(List<String> ids);

    FSFolderDto saveFolder(FSFolderDto dto);
}
