package com.arextest.web.core.repository;


import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;

import java.util.List;
import java.util.Set;

public interface FSInterfaceRepository extends RepositoryProvider {
    String initInterface(String parentId, Integer parentNodeType, String workspaceId);

    Boolean removeInterface(String id);

    Boolean removeInterfaces(Set<String> ids);

    FSInterfaceDto saveInterface(FSInterfaceDto interfaceDto);

    FSInterfaceDto queryInterface(String id);

    List<FSItemDto> queryInterfaces(Set<String> ids);

    String duplicate(FSInterfaceDto dto);
}
