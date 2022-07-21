package com.arextest.report.core.repository;


import com.arextest.report.model.dto.filesystem.FSInterfaceDto;

import java.util.List;
import java.util.Set;

public interface FSInterfaceRepository extends RepositoryProvider {
    String initInterface(String parentId, Integer parentNodeType);

    Boolean removeInterface(String id);

    FSInterfaceDto saveInterface(FSInterfaceDto interfaceDto);

    FSInterfaceDto queryInterface(String id);

    List<FSInterfaceDto> queryInterfaces(Set<String> ids);

    String duplicate(FSInterfaceDto dto);
}
