package com.arextest.web.core.repository;

import com.arextest.web.model.dto.LabelDto;
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
