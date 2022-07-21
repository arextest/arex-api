package com.arextest.report.core.repository;


import com.arextest.report.model.dto.filesystem.FSFolderDto;

public interface FSFolderRepository extends RepositoryProvider {
    String initFolder(String parentId, Integer parentNodeType);

    Boolean removeFolder(String id);

    FSFolderDto queryById(String id);

    String duplicate(FSFolderDto dto);
}
