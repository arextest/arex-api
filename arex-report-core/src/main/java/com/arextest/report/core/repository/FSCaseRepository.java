package com.arextest.report.core.repository;

import com.arextest.report.model.dto.filesystem.FSCaseDto;
import com.arextest.report.model.dto.filesystem.FSItemDto;

import java.util.List;
import java.util.Set;


public interface FSCaseRepository extends RepositoryProvider {
    String initCase(String parentId, Integer parentNodeType, String workspaceId);

    Boolean removeCase(String id);

    Boolean removeCases(Set<String> ids);

    FSCaseDto saveCase(FSCaseDto dto);

    FSCaseDto queryCase(String id);

    List<FSItemDto> queryCases(List<String> ids);

    String duplicate(FSCaseDto dto);
}
