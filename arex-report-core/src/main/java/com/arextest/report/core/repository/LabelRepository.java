package com.arextest.report.core.repository;

import com.arextest.report.model.dto.LabelDto;

import java.util.List;

/**
 * @author b_yu
 * @since 2022/11/17
 */
public interface LabelRepository extends RepositoryProvider {
    boolean saveLabel(LabelDto dto);
    boolean removeLabel(String labelId);
    List<LabelDto> queryLabelsByWorkspaceId(String workspaceId);
}
