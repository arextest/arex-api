package com.arextest.web.core.business.filesystem.importexport.impl;

import com.arextest.common.exceptions.ArexException;
import com.arextest.web.common.LogUtils;
import com.arextest.web.common.exception.ArexApiResponseCode;
import com.arextest.web.core.business.filesystem.FileSystemUtils;
import com.arextest.web.core.business.filesystem.ItemInfo;
import com.arextest.web.core.business.filesystem.ItemInfoFactory;
import com.arextest.web.core.business.filesystem.importexport.ImportExport;
import com.arextest.web.core.repository.FSTreeRepository;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FSFolderDto;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import com.arextest.web.model.dto.filesystem.importexport.CaseItemDto;
import com.arextest.web.model.dto.filesystem.importexport.FolderItemDto;
import com.arextest.web.model.dto.filesystem.importexport.InterfaceItemDto;
import com.arextest.web.model.dto.filesystem.importexport.Item;
import com.arextest.web.model.dto.filesystem.importexport.ItemCollectionDto;
import com.arextest.web.model.enums.FSInfoItem;
import com.arextest.web.model.mapper.FSCaseMapper;
import com.arextest.web.model.mapper.FSFolderMapper;
import com.arextest.web.model.mapper.FSInterfaceMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

/**
 * @author b_yu
 * @since 2022/9/28
 */
@Slf4j
@Component("ImportExport-1")
public class InternalImportExportImpl implements ImportExport {

  @Resource
  private FileSystemUtils fileSystemUtils;

  @Resource
  private ObjectMapper objectMapper;

  @Resource
  private ItemInfoFactory itemInfoFactory;

  @Resource
  private FSTreeRepository fsTreeRepository;

  @Override
  public boolean importItem(FSTreeDto fsTreeDto, List<String> path, String importString) {
    List<FSNodeDto> nodes = new ArrayList<>();
    ItemCollectionDto collection = null;
    try {
      collection = objectMapper.readValue(importString, ItemCollectionDto.class);
    } catch (JsonProcessingException e) {
      throw new ArexException(ArexApiResponseCode.FS_FORMAT_ERROR, "Failed to import items", e);
    }

    if (collection == null) {
      return false;
    }

    if (!checkCollection(collection)) {
      LOGGER.error("Failed to import items, collection's format is not correct");
      return false;
    }

    FSNodeDto parent = null;
    if (CollectionUtils.isNotEmpty(path)) {
      parent = fileSystemUtils.findByPath(fsTreeDto.getRoots(), path);
    }
    for (Item item : collection.getItems()) {
      if (parent != null) {
        nodes.add(
            convertToWorkspaceItem(fsTreeDto.getId(), parent.getInfoId(), parent.getNodeType(),
                item));
      } else {
        nodes.add(convertToWorkspaceItem(fsTreeDto.getId(), null, null, item));
      }

    }

    if (parent != null) {
      if (CollectionUtils.isEmpty(parent.getChildren())) {
        parent.setChildren(new ArrayList<>());
      }
      parent.getChildren().addAll(nodes);
    } else {
      if (CollectionUtils.isEmpty(fsTreeDto.getRoots())) {
        fsTreeDto.setRoots(new ArrayList<>());
      }
      fsTreeDto.getRoots().addAll(nodes);
    }
    fsTreeRepository.updateFSTree(fsTreeDto);
    return true;
  }

  @Override
  public String exportItem(List<FSNodeDto> nodes, Map<String, FSItemDto> itemInfos) {
    if (CollectionUtils.isEmpty(nodes) || MapUtils.isEmpty(itemInfos)) {
      return Strings.EMPTY;
    }
    ItemCollectionDto collectionDto = new ItemCollectionDto();
    collectionDto.setItems(new ArrayList<>(nodes.size()));
    for (FSNodeDto nodeDto : nodes) {
      Item item = convertToImportExportItem(nodeDto, itemInfos);
      if (item == null) {
        continue;
      }
      collectionDto.getItems().add(item);
    }
    try {
      return objectMapper.writeValueAsString(collectionDto);
    } catch (JsonProcessingException e) {
      LogUtils.error(LOGGER, "Failed to export items", e);
    }
    return StringUtils.EMPTY;
  }

  private FSNodeDto convertToWorkspaceItem(String workspaceId, String parentId,
      Integer parentNodeType, Item item) {
    FSItemDto fsItemDto;
    String method = null;
    FSNodeDto node = new FSNodeDto();
    switch (item.getNodeType()) {
      case FSInfoItem.INTERFACE:
        fsItemDto = FSInterfaceMapper.INSTANCE.fsItemFromIeItemDto((InterfaceItemDto) item);
        method = ((InterfaceItemDto) item).getAddress().getMethod();
        break;
      case FSInfoItem.CASE:
        fsItemDto = FSCaseMapper.INSTANCE.fsItemFromIeItemDto((CaseItemDto) item);
        node.setCaseSourceType(((CaseItemDto) item).getCaseSourceType());
        break;
      case FSInfoItem.FOLDER:
        fsItemDto = FSFolderMapper.INSTANCE.fsItemFromIeItemDto((FolderItemDto) item);
        break;
      default:
        LogUtils.error(LOGGER, "Unexpected NodeType:{}", item.getNodeType());
        return null;
    }

    fsItemDto.setWorkspaceId(workspaceId);
    fsItemDto.setParentId(parentId);
    fsItemDto.setParentNodeType(parentNodeType);
    ItemInfo itemInfo = itemInfoFactory.getItemInfo(item.getNodeType());
    String id = itemInfo.saveItem(fsItemDto);

    node.setInfoId(id);
    node.setNodeName(item.getNodeName());
    node.setNodeType(item.getNodeType());
    node.setLabelIds(item.getLabelIds());
    node.setMethod(method);

    if (CollectionUtils.isNotEmpty(item.getItems())) {
      node.setChildren(new ArrayList<>(item.getItems().size()));
      for (Item i : item.getItems()) {
        FSNodeDto child = convertToWorkspaceItem(workspaceId, id, item.getNodeType(), i);
        node.getChildren().add(child);
      }
    }
    return node;
  }

  private Item convertToImportExportItem(FSNodeDto node, Map<String, FSItemDto> itemInfos) {
    FSItemDto fsItemDto = itemInfos.get(node.getInfoId());
    if (fsItemDto == null) {
      return null;
    }

    Item item;
    switch (node.getNodeType()) {
      case FSInfoItem.INTERFACE:
        item = FSInterfaceMapper.INSTANCE.ieItemFromFsItemDto((FSInterfaceDto) fsItemDto);
        break;
      case FSInfoItem.CASE:
        item = FSCaseMapper.INSTANCE.ieItemFromFsItemDto((FSCaseDto) fsItemDto);
        ((CaseItemDto) item).setCaseSourceType(node.getCaseSourceType());
        break;
      case FSInfoItem.FOLDER:
        item = FSFolderMapper.INSTANCE.ieItemFromFsItemDto((FSFolderDto) fsItemDto);
        break;
      default:
        LogUtils.error(LOGGER, "Unexpected NodeType:{}", node.getNodeType());
        return null;
    }
    item.setNodeType(node.getNodeType());
    item.setNodeName(node.getNodeName());
    if (CollectionUtils.isNotEmpty(node.getChildren())) {
      for (FSNodeDto child : node.getChildren()) {
        if (CollectionUtils.isEmpty(item.getItems())) {
          item.setItems(new ArrayList<>(node.getChildren().size()));
        }
        item.getItems().add(convertToImportExportItem(child, itemInfos));
      }
    }
    return item;
  }

  // true: collection is correct, false: collection is not correct
  private boolean checkCollection(ItemCollectionDto collection) {
    if (collection == null || collection.getItems() == null) {
      return false;
    }
    collection.getItems().removeIf(Objects::isNull);
    if (CollectionUtils.isEmpty(collection.getItems())) {
      return false;
    }
    for (Item item : collection.getItems()) {
      if (!FSInfoItem.ALL_TYPES.contains(item.getNodeType())) {
        return false;
      }
    }
    return true;
  }
}
