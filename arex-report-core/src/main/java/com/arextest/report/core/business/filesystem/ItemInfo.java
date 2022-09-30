package com.arextest.report.core.business.filesystem;


import com.arextest.report.model.dto.filesystem.FSItemDto;

import java.util.List;
import java.util.Set;

public interface ItemInfo {
    String saveItem(String parentId, Integer parentNodeType, String workspaceId);
    Boolean removeItem(String infoId);
    Boolean removeItems(Set<String> infoIds);
    String duplicate(String parentId, String infoId);
    List<FSItemDto> queryByIds(List<String> ids);
}
