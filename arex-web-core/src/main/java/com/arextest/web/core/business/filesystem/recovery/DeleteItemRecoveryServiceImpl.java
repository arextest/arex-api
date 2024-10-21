package com.arextest.web.core.business.filesystem.recovery;

import com.arextest.web.core.business.filesystem.FileSystemUtils;
import com.arextest.web.core.business.filesystem.ItemInfo;
import com.arextest.web.core.business.filesystem.ItemInfoFactory;
import com.arextest.web.core.repository.FSTreeRepository;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTraceLogDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2023/2/7
 */
@Component("Recovery-3")
public class DeleteItemRecoveryServiceImpl implements RecoveryService {

  @Resource
  private FSTreeRepository fsTreeRepository;

  @Resource
  private FileSystemUtils fileSystemUtils;

  @Resource
  private ItemInfoFactory itemInfoFactory;

  @Override
  public boolean recovery(FSTraceLogDto log) {
    FSTreeDto fsTreeDto = fsTreeRepository.queryFSTreeById(log.getWorkspaceId());
    FSNodeDto fsNodeDto = fileSystemUtils.deepFindByInfoId(fsTreeDto.getRoots(), log.getParentId());
    if (fsNodeDto == null) {
      if (fsTreeDto.getRoots() == null) {
        fsTreeDto.setRoots(new ArrayList<>());
      }
      fsTreeDto.getRoots().add(log.getNode());
    } else {
      if (fsNodeDto.getChildren() == null) {
        fsNodeDto.setChildren(new ArrayList<>());
      }
      fsNodeDto.getChildren().add(0, log.getNode());
    }
    fsTreeRepository.updateFSTree(fsTreeDto);

    Map<String, FSItemDto> itemDtoMap =
        log.getItems().stream().collect(Collectors.toMap(FSItemDto::getId, Function.identity()));
    Map<Integer, List<FSItemDto>> tmp = new HashMap<>();

    Queue<FSNodeDto> queue = new ArrayDeque<>();
    queue.add(log.getNode());
    while (!queue.isEmpty()) {
      FSNodeDto node = queue.poll();
      if (!itemDtoMap.containsKey(node.getInfoId())) {
        continue;
      }
      if (!tmp.containsKey(node.getNodeType())) {
        tmp.put(node.getNodeType(), new ArrayList<>());
      }

      tmp.get(node.getNodeType()).add(itemDtoMap.get(node.getInfoId()));
      if (CollectionUtils.isNotEmpty(node.getChildren())) {
        queue.addAll(node.getChildren());
      }
    }

    saveItems(tmp);
    return true;
  }

  @Async("recovery-items-executor")
  public void saveItems(Map<Integer, List<FSItemDto>> itemMap) {
    for (Map.Entry<Integer, List<FSItemDto>> entry : itemMap.entrySet()) {
      ItemInfo itemInfo = itemInfoFactory.getItemInfo(entry.getKey());
      for (FSItemDto itemDto : entry.getValue()) {
        itemInfo.saveItem(itemDto);
      }
    }
  }
}
