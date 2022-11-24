package com.arextest.web.core.repository;


import com.arextest.web.model.dto.filesystem.FSTreeDto;

import java.util.List;
import java.util.Set;

public interface FSTreeRepository extends RepositoryProvider {
    FSTreeDto initFSTree(FSTreeDto dto);

    FSTreeDto updateFSTree(FSTreeDto dto);

    FSTreeDto queryFSTreeById(String id);

    List<FSTreeDto> queryFSTreeByIds(Set<String> ids);

    Boolean deleteFSTree(String id);
}
