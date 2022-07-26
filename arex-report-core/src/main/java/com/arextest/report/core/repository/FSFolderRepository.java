package com.arextest.report.core.repository;


import com.arextest.report.model.dto.filesystem.FSFolderDto;

import java.util.Set;

public interface FSFolderRepository extends RepositoryProvider {
    String initFolder(String parentId, Integer parentNodeType);

    Boolean removeFolder(String id);

    Boolean removeFolders(Set<String> ids);

    FSFolderDto queryById(String id);

    String duplicate(FSFolderDto dto);
}
