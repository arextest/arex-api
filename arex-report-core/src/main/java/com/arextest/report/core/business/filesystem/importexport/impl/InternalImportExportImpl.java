package com.arextest.report.core.business.filesystem.importexport.impl;

import com.arextest.report.core.business.filesystem.importexport.ImportExport;
import com.arextest.report.model.dto.filesystem.FSItemDto;
import com.arextest.report.model.dto.filesystem.FSNodeDto;
import com.arextest.report.model.dto.filesystem.importexport.Item;
import com.arextest.report.model.dto.filesystem.importexport.ItemCollectionDto;
import com.arextest.report.model.enums.FSInfoItem;
import com.arextest.report.model.mapper.FSCaseMapper;
import com.arextest.report.model.mapper.FSFolderMapper;
import com.arextest.report.model.mapper.FSInterfaceMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author b_yu
 * @since 2022/9/28
 */
@Slf4j
@Component("1")
public class InternalImportExportImpl implements ImportExport {
    @Override
    public String export(List<FSNodeDto> nodes, Map<String, FSItemDto> itemInfos) {
        if (CollectionUtils.isEmpty(nodes) || MapUtils.isEmpty(itemInfos)) {
            return Strings.EMPTY;
        }
        ItemCollectionDto collectionDto = new ItemCollectionDto();
        collectionDto.setItems(new ArrayList<>(nodes.size()));
        for (FSNodeDto nodeDto : nodes) {
            collectionDto.getItems().add(convert(nodeDto, itemInfos));
        }
    }
    @Override
    public void Import() {

    }

    private Item convert(FSNodeDto node, Map<String, FSItemDto> itemInfos) {
        FSItemDto fsItemDto = itemInfos.get(node.getInfoId());
        if (fsItemDto == null) {
            return null;
        }

        Item item = null;
        switch (node.getNodeType()) {
            case FSInfoItem.INTERFACE:
                item = FSInterfaceMapper.INSTANCE.convertFromFsItemDto(fsItemDto);
                break;
            case FSInfoItem.CASE:
                item = FSCaseMapper.INSTANCE.convertFromFsItemDto(fsItemDto);
                break;
            case FSInfoItem.FOLDER:
                item = FSFolderMapper.INSTANCE.convertFromFsItemDto(fsItemDto);
                break;
            default:
                LOGGER.error("Unexpected NodeType:{}", node.getNodeType());
                return null;
        }
        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            for (FSNodeDto child : node.getChildren()) {
                if (CollectionUtils.isEmpty(item.getItems())) {
                    item.setItems(new ArrayList<>(node.getChildren().size()));
                }
                item.getItems().add(convert(child, itemInfos));
            }
        }
        return item;
    }
}
