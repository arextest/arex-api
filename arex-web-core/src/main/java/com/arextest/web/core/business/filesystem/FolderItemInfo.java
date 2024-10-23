package com.arextest.web.core.business.filesystem;

import com.arextest.web.core.repository.FSFolderRepository;
import com.arextest.web.model.dto.filesystem.FSFolderDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import java.util.List;
import java.util.Set;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component("ItemInfo-3")
public class FolderItemInfo implements ItemInfo {

  @Resource
  private FSFolderRepository fsFolderRepository;

  @Override
  public String initItem(String parentId, Integer parentNodeType, String workspaceId, String name) {
    return fsFolderRepository.initFolder(parentId, parentNodeType, workspaceId, name);
  }

  @Override
  public String saveItem(FSItemDto dto) {
    FSFolderDto folderDto = fsFolderRepository.saveFolder((FSFolderDto) dto);
    return folderDto.getId();
  }

  @Override
  public Boolean removeItem(String infoId) {
    return fsFolderRepository.removeFolder(infoId);
  }

  @Override
  public Boolean removeItems(Set<String> infoIds) {
    return fsFolderRepository.removeFolders(infoIds);
  }

  @Override
  public String duplicate(String parentId, String infoId, String name) {
    FSFolderDto dto = fsFolderRepository.queryById(infoId);
    dto.setId(null);
    dto.setParentId(parentId);
    dto.setName(name);
    return fsFolderRepository.duplicate(dto);
  }

  @Override
  public List<FSItemDto> queryByIds(List<String> ids) {
    return fsFolderRepository.queryByIds(ids);
  }

  @Override
  public FSItemDto queryById(String id) {
    return fsFolderRepository.queryById(id);
  }
}
