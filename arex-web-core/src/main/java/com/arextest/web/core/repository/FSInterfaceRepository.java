package com.arextest.web.core.repository;

import java.util.List;
import java.util.Set;

import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;

public interface FSInterfaceRepository extends RepositoryProvider {
    String initInterface(String parentId, Integer parentNodeType, String workspaceId, String name);

    Boolean removeInterface(String id);

    Boolean removeInterfaces(Set<String> ids);

    FSInterfaceDto saveInterface(FSInterfaceDto interfaceDto);

    FSInterfaceDto queryInterface(String id);

    List<FSItemDto> queryInterfaces(Set<String> ids);

    String duplicate(FSInterfaceDto dto);
}
