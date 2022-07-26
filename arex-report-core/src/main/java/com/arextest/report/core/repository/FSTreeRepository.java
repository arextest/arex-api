package com.arextest.report.core.repository;


import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.dto.filesystem.FSTreeDto;
import com.arextest.report.model.dto.WorkspaceDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FSTreeRepository extends RepositoryProvider {
    FSTreeDto initFSTree(FSTreeDto dto);

    FSTreeDto updateFSTree(FSTreeDto dto);

    FSTreeDto queryFSTreeById(String id);

    List<WorkspaceDto> queryWorkspacesByUser(String userName);

    Boolean deleteFSTree(String id);
}
