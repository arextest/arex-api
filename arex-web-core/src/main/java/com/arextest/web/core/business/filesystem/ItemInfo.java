package com.arextest.web.core.business.filesystem;

import java.util.List;
import java.util.Set;

import com.arextest.web.model.dto.filesystem.FSItemDto;

public interface ItemInfo {
    String initItem(String parentId, Integer parentNodeType, String workspaceId, String name);

    String saveItem(FSItemDto dto);

    Boolean removeItem(String infoId);

    Boolean removeItems(Set<String> infoIds);

    String duplicate(String parentId, String infoId, String name);

    List<FSItemDto> queryByIds(List<String> ids);

    FSItemDto queryById(String id);
}
