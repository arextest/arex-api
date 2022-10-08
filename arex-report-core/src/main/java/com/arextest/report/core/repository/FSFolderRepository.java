package com.arextest.report.core.repository;


import com.arextest.report.model.dto.filesystem.FSFolderDto;
import com.arextest.report.model.dto.filesystem.FSItemDto;

import java.util.List;
import java.util.Set;

public interface FSFolderRepository extends RepositoryProvider {
    String initFolder(String parentId, Integer parentNodeType, String workspaceId);

    Boolean removeFolder(String id);

    Boolean removeFolders(Set<String> ids);

    FSFolderDto queryById(String id);

    String duplicate(FSFolderDto dto);

    List<FSItemDto> queryByIds(List<String> ids);

    FSFolderDto saveFolder(FSFolderDto dto);
}
