package com.arextest.report.core.business.filesystem;

import com.arextest.report.core.repository.FSInterfaceRepository;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.dto.filesystem.FSItemDto;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("1")
public class InterfaceItemInfo implements ItemInfo {

    @Resource
    private FSInterfaceRepository fsInterfaceRepository;

    @Override
    public String saveItem(String parentId, Integer parentNodeType, String workspaceId) {
        return fsInterfaceRepository.initInterface(parentId, parentNodeType, workspaceId);
    }
    @Override
    public Boolean removeItem(String infoId) {
        return fsInterfaceRepository.removeInterface(infoId);
    }
    @Override
    public Boolean removeItems(Set<String> infoIds) {
        return fsInterfaceRepository.removeInterfaces(infoIds);
    }
    @Override
    public String duplicate(String parentId, String infoId) {
        FSInterfaceDto dto = fsInterfaceRepository.queryInterface(infoId);
        dto.setId(null);
        dto.setParentId(parentId);
        return fsInterfaceRepository.duplicate(dto);
    }
    @Override
    public List<FSItemDto> queryByIds(List<String> ids) {
        return fsInterfaceRepository.queryInterfaces(new HashSet<>(ids));
    }
}
