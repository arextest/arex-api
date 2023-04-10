package com.arextest.web.core.repository;


import com.arextest.web.model.dto.filesystem.FSTreeDto;

import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface FSTreeRepository extends RepositoryProvider {
    FSTreeDto initFSTree(FSTreeDto dto);

    FSTreeDto updateFSTree(FSTreeDto dto);

    FSTreeDto updateFSTree(String workspaceId, UnaryOperator<FSTreeDto> unaryOperator);

    FSTreeDto queryFSTreeById(String id);

    List<FSTreeDto> queryFSTreeByIds(Set<String> ids);

    Boolean deleteFSTree(String id);
}
