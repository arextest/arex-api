package com.arextest.report.core.repository;


import com.arextest.report.model.dto.filesystem.FSInterfaceDto;

public interface FSInterfaceRepository extends RepositoryProvider {
    String initInterface();

    Boolean removeInterface(String id);

    FSInterfaceDto saveInterface(FSInterfaceDto interfaceDto);
}
