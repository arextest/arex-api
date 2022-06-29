package com.arextest.report.core.repository;


import com.arextest.report.model.dto.filesystem.FSInterfaceDto;

public interface FSInterfaceRepository extends RepositoryProvider {
    String initInterface(String parentId, Integer parentNodeType);

    Boolean removeInterface(String id);

    FSInterfaceDto saveInterface(FSInterfaceDto interfaceDto);

    FSInterfaceDto queryInterface(String id);
}
