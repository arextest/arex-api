package com.arextest.report.core.repository;


public interface FSFolderRepository extends RepositoryProvider {
    String initFolder(String parentId, Integer parentNodeType);
    Boolean removeFolder(String id);
}
