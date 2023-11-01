package com.arextest.web.core.business.filesystem;

import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component("ItemInfo-1")
public class InterfaceItemInfo implements ItemInfo {

  @Resource
  private FSInterfaceRepository fsInterfaceRepository;

  @Override
  public String initItem(String parentId, Integer parentNodeType, String workspaceId, String name) {
    return fsInterfaceRepository.initInterface(parentId, parentNodeType, workspaceId, name);
  }

  @Override
  public String saveItem(FSItemDto dto) {
    FSInterfaceDto interfaceDto = fsInterfaceRepository.saveInterface((FSInterfaceDto) dto);
    return interfaceDto.getId();
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
  public String duplicate(String parentId, String infoId, String name) {
    FSInterfaceDto dto = fsInterfaceRepository.queryInterface(infoId);
    dto.setId(null);
    dto.setParentId(parentId);
    dto.setName(name);
    return fsInterfaceRepository.duplicate(dto);
  }

  @Override
  public List<FSItemDto> queryByIds(List<String> ids) {
    return fsInterfaceRepository.queryInterfaces(new HashSet<>(ids));
  }

  @Override
  public FSItemDto queryById(String id) {
    return fsInterfaceRepository.queryInterface(id);
  }
}
