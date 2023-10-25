package com.arextest.web.core.business.filesystem.importexport.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.constants.NetworkConstants;
import com.arextest.web.core.business.filesystem.FileSystemUtils;
import com.arextest.web.core.business.filesystem.ItemInfo;
import com.arextest.web.core.business.filesystem.ItemInfoFactory;
import com.arextest.web.core.business.filesystem.importexport.ImportExport;
import com.arextest.web.core.business.filesystem.importexport.postmancollection.Collection;
import com.arextest.web.core.business.filesystem.importexport.postmancollection.CollectionItem;
import com.arextest.web.core.business.filesystem.importexport.postmancollection.ItemBody;
import com.arextest.web.core.business.filesystem.importexport.postmancollection.ItemRequest;
import com.arextest.web.core.business.filesystem.importexport.postmancollection.ItemResponse;
import com.arextest.web.core.business.filesystem.importexport.postmancollection.ItemUrl;
import com.arextest.web.core.repository.FSTreeRepository;
import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.BodyDto;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FSFolderDto;
import com.arextest.web.model.dto.filesystem.FSInterfaceAndCaseBaseDto;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import com.arextest.web.model.dto.filesystem.ScriptBlockDto;
import com.arextest.web.model.enums.CaseSourceType;
import com.arextest.web.model.enums.FSInfoItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ImportExport-2")
public class PostmanImportExportImpl implements ImportExport {

    private static final String PRE_REQUEST_LISTEN = "prerequest";
    private static final String TEST_LISTEN = "test";
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private FileSystemUtils fileSystemUtils;
    @Resource
    private ItemInfoFactory itemInfoFactory;
    @Resource
    private FSTreeRepository fsTreeRepository;

    @Override
    public boolean importItem(FSTreeDto fsTreeDto, String[] path, String importString) {
        Collection collection = null;
        try {
            collection = objectMapper.readValue(importString, Collection.class);
        } catch (JsonProcessingException e) {
            LogUtils.error(LOGGER, "Failed to import items", e);
        }

        if (collection == null) {
            return false;
        }

        FSNodeDto parent = null;
        if (ArrayUtils.isNotEmpty(path)) {
            parent = fileSystemUtils.findByPath(fsTreeDto.getRoots(), path);
        }

        FSNodeDto folder;
        if (parent != null) {
            if (CollectionUtils.isEmpty(parent.getChildren())) {
                parent.setChildren(new ArrayList<>());
            }
            folder = createRootFolder(collection, fsTreeDto.getId(), parent.getInfoId(), parent.getNodeType());
            parent.getChildren().add(folder);
        } else {
            if (CollectionUtils.isEmpty(fsTreeDto.getRoots())) {
                fsTreeDto.setRoots(new ArrayList<>());
            }
            folder = createRootFolder(collection, fsTreeDto.getId(), null, null);
            fsTreeDto.getRoots().add(folder);
        }

        fsTreeRepository.updateFSTree(fsTreeDto);
        return true;
    }

    @Override
    public String exportItem(List<FSNodeDto> nodes, Map<String, FSItemDto> itemInfos) {
        LOGGER.error("Temporarily not supported");
        return null;
    }

    private FSNodeDto createRootFolder(Collection collection, String workSpaceId, String parentId,
        Integer parentNodeType) {
        FSNodeDto folderNode = new FSNodeDto();
        folderNode.setNodeName(collection.getInfo().getName());
        folderNode.setNodeType(FSInfoItem.FOLDER);

        FSFolderDto fsFolderDto = new FSFolderDto();
        fsFolderDto.setName(folderNode.getNodeName());
        return getFolderNode(workSpaceId, parentId, parentNodeType, folderNode, fsFolderDto, collection.getItem());
    }

    private FSNodeDto getFolderNode(String workSpaceId, String parentId, Integer parentNodeType, FSNodeDto folderNode,
        FSFolderDto fsFolderDto, List<CollectionItem> items) {
        fsFolderDto.setParentNodeType(parentNodeType);
        fsFolderDto.setParentId(parentId);
        fsFolderDto.setWorkspaceId(workSpaceId);
        ItemInfo itemInfo = itemInfoFactory.getItemInfo(folderNode.getNodeType());
        String id = itemInfo.saveItem(fsFolderDto);
        folderNode.setInfoId(id);

        List<FSNodeDto> children = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            items.forEach(collectionItem -> {
                if (collectionItem.getRequest() == null) {
                    children.add(createFolderNode(collectionItem, workSpaceId, id, folderNode.getNodeType()));
                } else {
                    children.add(createInterfaceNode(collectionItem, workSpaceId, id, folderNode.getNodeType()));
                }
            });
        }
        folderNode.setChildren(children);
        return folderNode;
    }

    private FSNodeDto createFolderNode(CollectionItem item, String workSpaceId, String parentId,
        Integer parentNodeType) {
        FSNodeDto folderNode = new FSNodeDto();
        folderNode.setNodeName(item.getName());
        folderNode.setNodeType(FSInfoItem.FOLDER);

        FSFolderDto fsFolderDto = new FSFolderDto();
        fsFolderDto.setName(item.getName());
        return getFolderNode(workSpaceId, parentId, parentNodeType, folderNode, fsFolderDto, item.getItem());
    }

    private FSNodeDto createInterfaceNode(CollectionItem item, String workSpaceId, String parentId,
        Integer parentNodeType) {
        final ItemRequest itemRequest = item.getRequest();
        FSNodeDto interfaceNode = new FSNodeDto();
        interfaceNode.setNodeName(item.getName());
        interfaceNode.setNodeType(FSInfoItem.INTERFACE);
        interfaceNode.setMethod(itemRequest.getMethod());

        FSInterfaceDto fsInterfaceDto =
            createFSInterfaceAndCaseBaseDto(itemRequest, workSpaceId, parentId, parentNodeType, new FSInterfaceDto());
        fsInterfaceDto.setName(item.getName());

        // script
        if (item.getEvent() != null) {
            List<ScriptBlockDto> preRequestScripts = new ArrayList<>();
            List<ScriptBlockDto> testScripts = new ArrayList<>();
            item.getEvent().forEach(collectionEvent -> {
                ScriptBlockDto scriptBlockDto = new ScriptBlockDto();
                if (PRE_REQUEST_LISTEN.equals(collectionEvent.getListen())) {
                    scriptBlockDto.setDisabled(false);
                    scriptBlockDto.setValue(scriptTransform(collectionEvent.getScript().getExec()));
                    preRequestScripts.add(scriptBlockDto);
                } else if (TEST_LISTEN.equals(collectionEvent.getListen())) {
                    scriptBlockDto.setDisabled(false);
                    scriptBlockDto.setValue(scriptTransform(collectionEvent.getScript().getExec()));
                    testScripts.add(scriptBlockDto);
                }
            });
            fsInterfaceDto.setPreRequestScripts(preRequestScripts);
            fsInterfaceDto.setTestScripts(testScripts);
        }

        // body
        if (itemRequest.getBody() != null) {
            BodyDto bodyDto = new BodyDto();
            if (StringUtils.equalsIgnoreCase(itemRequest.getBody().getMode(), NetworkConstants.RAW)) {
                bodyDto.setBody(itemRequest.getBody().getRaw());
            }
            String language = Optional.ofNullable(itemRequest.getBody()).map(ItemBody::getOptions)
                .map(ItemBody.BodyOptions::getRaw).map(ItemBody.OptionsRaw::getLanguage).orElse(null);
            if (StringUtils.equalsIgnoreCase(language, NetworkConstants.JSON)) {
                bodyDto.setContentType(NetworkConstants.APPLICATION_JSON);
            }
            fsInterfaceDto.setBody(bodyDto);
        }

        // auth: not used
        // testAddress:not used
        fsInterfaceDto.setDescription(itemRequest.getDescription());

        ItemInfo itemInfo = itemInfoFactory.getItemInfo(interfaceNode.getNodeType());
        String id = itemInfo.saveItem(fsInterfaceDto);
        interfaceNode.setInfoId(id);
        List<FSNodeDto> children = new ArrayList<>();
        item.getResponse().forEach(itemResponse -> {
            children.add(createCaseNode(itemResponse, workSpaceId, id, interfaceNode.getNodeType()));
        });
        interfaceNode.setChildren(children);
        return interfaceNode;
    }

    private FSNodeDto createCaseNode(ItemResponse itemResponse, String workSpaceId, String parentId,
        Integer parentNodeType) {
        final ItemRequest itemRequest = itemResponse.getOriginalRequest();
        FSNodeDto caseNode = new FSNodeDto();
        caseNode.setNodeName(itemResponse.getName());
        caseNode.setNodeType(FSInfoItem.CASE);
        caseNode.setCaseSourceType(CaseSourceType.MANUAL_CASE);

        FSCaseDto fsCaseDto =
            createFSInterfaceAndCaseBaseDto(itemRequest, workSpaceId, parentId, parentNodeType, new FSCaseDto());
        fsCaseDto.setName(caseNode.getNodeName());

        ItemInfo itemInfo = itemInfoFactory.getItemInfo(caseNode.getNodeType());
        String id = itemInfo.saveItem(fsCaseDto);
        caseNode.setInfoId(id);
        return caseNode;
    }

    private <T extends FSInterfaceAndCaseBaseDto> T createFSInterfaceAndCaseBaseDto(ItemRequest itemRequest,
        String workSpaceId, String parentId, Integer parentNodeType, T input) {
        input.setParentNodeType(parentNodeType);
        input.setParentId(parentId);
        input.setWorkspaceId(workSpaceId);

        // address
        AddressDto addressDto = new AddressDto();
        addressDto.setMethod(itemRequest.getMethod());
        if (itemRequest.getUrl() != null) {
            addressDto.setEndpoint(itemRequest.getUrl().getRaw());
        }
        input.setAddress(addressDto);

        // header & param
        List<KeyValuePairDto> headers = new ArrayList<>();
        List<KeyValuePairDto> params = new ArrayList<>();
        if (itemRequest.getHeader() != null) {
            itemRequest.getHeader().forEach(header -> {
                KeyValuePairDto keyValuePairDto = new KeyValuePairDto();
                keyValuePairDto.setKey(header.getKey());
                keyValuePairDto.setValue(header.getValue());
                keyValuePairDto.setActive(!header.getDisabled());
                headers.add(keyValuePairDto);
            });
        }
        Optional.ofNullable(itemRequest.getUrl()).map(ItemUrl::getQuery)
            .ifPresent(queryList -> queryList.forEach(query -> {
                KeyValuePairDto keyValuePairDto = new KeyValuePairDto();
                keyValuePairDto.setKey(query.getKey());
                keyValuePairDto.setValue(query.getValue());
                keyValuePairDto.setActive(Boolean.TRUE);
                params.add(keyValuePairDto);
            }));

        input.setHeaders(headers);
        input.setParams(params);
        return input;
    }

    private String scriptTransform(List<String> scripts) {
        if (CollectionUtils.isEmpty(scripts)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(scripts.get(0));
        for (int i = 1; i < scripts.size(); i++) {
            stringBuilder.append("\n");
            stringBuilder.append(scripts.get(i));
        }
        return stringBuilder.toString();
    }
}
