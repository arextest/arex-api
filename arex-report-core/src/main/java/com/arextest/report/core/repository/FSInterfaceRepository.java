package com.arextest.report.core.repository;


public interface FSInterfaceRepository extends RepositoryProvider {
    String initInterface();
    Boolean removeInterface(String id);
}
