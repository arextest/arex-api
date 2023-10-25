package com.arextest.web.core.repository;

import java.util.List;
import java.util.Set;

import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;

public interface FSCaseRepository extends RepositoryProvider {
    String initCase(String parentId, Integer parentNodeType, String workspaceId, String name);

    Boolean removeCase(String id);

    Boolean removeCases(Set<String> ids);

    FSCaseDto saveCase(FSCaseDto dto);

    boolean updateCase(FSCaseDto dto);

    FSCaseDto queryCase(String id, boolean getCompareMsg);

    List<FSItemDto> queryCases(List<String> ids, boolean getCompareMsg);

    String duplicate(FSCaseDto dto);
}
