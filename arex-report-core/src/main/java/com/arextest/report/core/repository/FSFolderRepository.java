package com.arextest.report.core.repository;


public interface FSFolderRepository extends RepositoryProvider {
    String initFolder();
    Boolean removeFolder(String id);
}
