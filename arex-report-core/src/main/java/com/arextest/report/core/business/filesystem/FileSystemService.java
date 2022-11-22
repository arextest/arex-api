package com.arextest.report.core.business.filesystem;

import com.arextest.report.common.JwtUtil;
import com.arextest.report.common.LoadResource;
import com.arextest.report.common.Tuple;
import com.arextest.report.core.business.filesystem.importexport.ImportExport;
import com.arextest.report.core.business.filesystem.importexport.impl.ImportExportFactory;
import com.arextest.report.core.business.util.MailUtils;
import com.arextest.report.core.repository.FSCaseRepository;
import com.arextest.report.core.repository.FSInterfaceRepository;
import com.arextest.report.core.repository.FSTreeRepository;
import com.arextest.report.core.repository.UserRepository;
import com.arextest.report.core.repository.UserWorkspaceRepository;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemFromRecordRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSAddWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddWorkspaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSDuplicateRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSExportItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSImportItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSMoveItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryCaseRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryCaseResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryInterfaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryInterfaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryUsersByWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryUsersByWorkspaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryWorkspaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryWorkspacesRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryWorkspacesResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSRemoveItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSRenameRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSRenameWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveCaseResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveInterfaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveInterfaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSTreeType;
import com.arextest.report.model.api.contracts.filesystem.InviteToWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.InviteToWorkspaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.LeaveWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.UserType;
import com.arextest.report.model.api.contracts.filesystem.ValidInvitationRequestType;
import com.arextest.report.model.api.contracts.filesystem.ValidInvitationResponseType;
import com.arextest.report.model.dto.UserDto;
import com.arextest.report.model.dto.WorkspaceDto;
import com.arextest.report.model.dto.filesystem.FSCaseDto;
import com.arextest.report.model.dto.filesystem.FSInterfaceDto;
import com.arextest.report.model.dto.filesystem.FSItemDto;
import com.arextest.report.model.dto.filesystem.FSNodeDto;
import com.arextest.report.model.dto.filesystem.FSTreeDto;
import com.arextest.report.model.dto.filesystem.UserWorkspaceDto;
import com.arextest.report.model.enums.FSInfoItem;
import com.arextest.report.model.enums.InvitationType;
import com.arextest.report.model.enums.RoleType;
import com.arextest.report.model.enums.SendEmailType;
import com.arextest.report.model.mapper.AddressMapper;
import com.arextest.report.model.mapper.FSCaseMapper;
import com.arextest.report.model.mapper.FSInterfaceMapper;
import com.arextest.report.model.mapper.FSTreeMapper;
import com.arextest.report.model.mapper.UserWorkspaceMapper;
import com.arextest.report.model.mapper.WorkspaceMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.internal.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileSystemService {
    private static final String DEFAULT_WORKSPACE_NAME = "MyWorkSpace";
    private static final String DEFAULT_INTERFACE_NAME = "Default Interface";
    private static final String DUPLICATE_SUFFIX = "_copy";
    private static final String INVITATION_EMAIL_TEMPLATE = "classpath:invitationEmailTemplate.htm";
    private static final String SOMEBODY_PLACEHOLDER = "{{somebody}}";
    private static final String WORKSPACE_NAME_PLACEHOLDER = "{{workspaceName}}";
    private static final String LINK_PLACEHOLDER = "{{link}}";
    private static final String GET_METHOD = "GET";

    @Value("${arex.ui.url}")
    private String arexUiUrl;

    @Resource
    private FSTreeRepository fsTreeRepository;

    @Resource
    private FSInterfaceRepository fsInterfaceRepository;

    @Resource
    private FSCaseRepository fsCaseRepository;

    @Resource
    private UserWorkspaceRepository userWorkspaceRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private ItemInfoFactory itemInfoFactory;

    @Resource
    private MailUtils mailUtils;

    @Resource
    private StorageCase storageCase;

    @Resource
    private LoadResource loadResource;

    @Resource
    private ImportExportFactory importExportFactory;

    @Resource
    private FileSystemUtils fileSystemUtils;


    public FSAddItemResponseType addItem(FSAddItemRequestType request) {
        FSAddItemResponseType response = new FSAddItemResponseType();

        ItemInfo itemInfo = itemInfoFactory.getItemInfo(request.getNodeType());
        if (itemInfo == null) {
            response.setSuccess(false);
            return response;
        }

        try {
            if (StringUtils.isEmpty(request.getWorkspaceName())) {
                request.setWorkspaceName(DEFAULT_WORKSPACE_NAME);
            }
            if (StringUtils.isEmpty(request.getUserName())) {
                request.setUserName(StringUtils.EMPTY);
            }

            FSTreeDto dto;
            if (StringUtils.isEmpty(request.getId())) {
                dto = addWorkspace(request.getWorkspaceName(), request.getUserName());
            } else {
                dto = fsTreeRepository.queryFSTreeById(request.getId());
            }

            String infoId = null;
            if (request.getParentPath() == null || request.getParentPath().length == 0) {
                FSNodeDto nodeDto = new FSNodeDto();
                nodeDto.setNodeName(request.getNodeName());
                infoId = itemInfo.initItem(null, null, dto.getId());
                nodeDto.setInfoId(infoId);
                nodeDto.setNodeType(request.getNodeType());
                if (request.getNodeType() == FSInfoItem.INTERFACE) {
                    nodeDto.setMethod(GET_METHOD);
                }
                dto.getRoots().add(0, nodeDto);
            } else {
                String[] nodes = request.getParentPath();

                FSNodeDto current = fileSystemUtils.findByInfoId(dto.getRoots(), nodes[0]);
                if (current == null) {
                    response.setSuccess(false);
                    return response;
                }

                boolean error = false;
                for (int i = 1; i < nodes.length; i++) {
                    String node = nodes[i];
                    if (current.getChildren() == null || current.getChildren().size() == 0) {
                        error = true;
                        break;
                    }
                    current = fileSystemUtils.findByInfoId(current.getChildren(), node);

                    if (current == null) {
                        error = true;
                        break;
                    }
                }
                if (!error) {
                    if (current.getChildren() == null) {
                        current.setChildren(new ArrayList<>());
                    }
                    FSNodeDto newNodeDto = new FSNodeDto();
                    newNodeDto.setNodeName(request.getNodeName());
                    infoId = itemInfo.initItem(current.getInfoId(), current.getNodeType(), dto.getId());
                    newNodeDto.setInfoId(infoId);
                    newNodeDto.setNodeType(request.getNodeType());
                    if (request.getNodeType() == FSInfoItem.INTERFACE) {
                        newNodeDto.setMethod(GET_METHOD);
                    }
                    current.getChildren().add(0, newNodeDto);

                } else {
                    response.setSuccess(false);
                    return response;
                }
            }
            dto = fsTreeRepository.updateFSTree(dto);
            response.setInfoId(infoId);
            response.setWorkspaceId(dto.getId());
            response.setSuccess(true);

        } catch (Exception e) {
            LOGGER.error("failed to add item to filesystem", e);
            response.setSuccess(false);
        }
        return response;
    }

    public Boolean removeItem(FSRemoveItemRequestType request) {
        FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getId());
        if (treeDto == null) {
            return false;
        }
        List<FSNodeDto> current = treeDto.getRoots();
        if (current == null) {
            return false;
        }

        String[] nodes = request.getRemoveNodePath();
        for (int i = 0; i < nodes.length - 1; i++) {

            String node = nodes[i];
            FSNodeDto find = fileSystemUtils.findByInfoId(current, node);

            if (find == null || find.getChildren() == null) {
                return false;
            }
            current = find.getChildren();
        }

        FSNodeDto needRemove = fileSystemUtils.findByInfoId(current, nodes[nodes.length - 1]);
        removeItems(needRemove);
        current.remove(needRemove);
        fsTreeRepository.updateFSTree(treeDto);
        return true;
    }

    public Boolean rename(FSRenameRequestType request) {

        FSTreeDto fsTreeDto = fsTreeRepository.queryFSTreeById(request.getId());
        if (fsTreeDto == null) {
            return false;
        }
        FSNodeDto dto = fileSystemUtils.findByPath(fsTreeDto.getRoots(), request.getPath());

        if (dto == null) {
            return false;
        }
        dto.setNodeName(request.getNewName());

        fsTreeRepository.updateFSTree(fsTreeDto);
        return true;
    }

    public Boolean duplicate(FSDuplicateRequestType request) {
        try {
            FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getId());
            FSNodeDto parent = null;
            FSNodeDto current;
            if (request.getPath().length != 1) {
                parent = fileSystemUtils.findByPath(treeDto.getRoots(),
                        Arrays.copyOfRange(request.getPath(), 0, request.getPath().length - 1));
                current = fileSystemUtils.findByInfoId(parent.getChildren(),
                        request.getPath()[request.getPath().length - 1]);
            } else {
                current = fileSystemUtils.findByInfoId(treeDto.getRoots(), request.getPath()[0]);
            }
            FSNodeDto dupNodeDto = duplicateInfo(parent == null ? null : parent.getInfoId(),
                    current.getNodeName() + DUPLICATE_SUFFIX,
                    current);
            if (parent == null) {
                treeDto.getRoots().add(dupNodeDto);
            } else {
                parent.getChildren().add(dupNodeDto);
            }
            fsTreeRepository.updateFSTree(treeDto);
            return true;
        } catch (Exception e) {
            LOGGER.error("failed to duplicate item", e);
            return false;
        }
    }

    public Boolean move(FSMoveItemRequestType request) {
        try {
            FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getId());
            Tuple<Integer, FSNodeDto> current =
                    fileSystemUtils.findByPathWithIndex(treeDto.getRoots(), request.getFromNodePath());
            if (current == null) {
                return false;
            }
            FSNodeDto fromParent = null;
            FSNodeDto toParent = null;
            if (request.getFromNodePath().length > 1) {
                fromParent = fileSystemUtils.findByPath(treeDto.getRoots(),
                        Arrays.copyOfRange(request.getFromNodePath(), 0, request.getFromNodePath().length - 1));
            }
            if (request.getToParentPath() != null && request.getToParentPath().length > 0) {
                toParent = fileSystemUtils.findByPath(treeDto.getRoots(), request.getToParentPath());
            }
            Integer toIndex = request.getToIndex() == null ? 0 : request.getToIndex();
            if (toParent == null) {
                treeDto.getRoots().add(toIndex, current.y);
            } else {
                if (toParent.getChildren() == null) {
                    toParent.setChildren(new ArrayList<>());
                }
                toParent.getChildren().add(toIndex, current.y);
            }
            if (fromParent == null && toParent == null) {
                if (request.getToIndex() < current.x) {
                    treeDto.getRoots().remove(current.x + 1);
                } else {
                    treeDto.getRoots().remove(current.x.intValue());
                }
            } else if (fromParent != null && toParent != null
                    && Objects.equals(fromParent.getInfoId(), toParent.getInfoId())) {
                if (request.getToIndex() < current.x) {
                    fromParent.getChildren().remove(current.x + 1);
                } else {
                    fromParent.getChildren().remove(current.x.intValue());
                }
            } else {
                if (fromParent == null) {
                    treeDto.getRoots().remove(current.x.intValue());
                } else {
                    fromParent.getChildren().remove(current.x.intValue());
                }
            }
            fsTreeRepository.updateFSTree(treeDto);
            return true;
        } catch (Exception e) {
            LOGGER.error("failed to move item", e);
            return false;
        }
    }

    public Boolean renameWorkspace(FSRenameWorkspaceRequestType request) {
        FSTreeDto treeDto = new FSTreeDto();
        treeDto.setId(request.getId());
        treeDto.setWorkspaceName(request.getWorkspaceName());
        try {
            fsTreeRepository.updateFSTree(treeDto);
            return true;
        } catch (Exception e) {
            LOGGER.error("failed to rename workspace", e);
            return false;
        }
    }

    public Boolean deleteWorkspace(String id) {
        FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(id);
        Map<Integer, Set<String>> itemMap = new HashMap<>();
        Queue<FSNodeDto> queue = new ArrayDeque<>(treeDto.getRoots());

        while (!queue.isEmpty()) {
            FSNodeDto node = queue.poll();
            if (!itemMap.containsKey(node.getNodeType())) {
                itemMap.put(node.getNodeType(), new HashSet<>());
            }
            itemMap.get(node.getNodeType()).add(node.getInfoId());
            if (node.getChildren() != null && node.getChildren().size() > 0) {
                queue.addAll(node.getChildren());
            }
        }
        if (itemMap.size() > 0) {
            for (Map.Entry<Integer, Set<String>> items : itemMap.entrySet()) {
                ItemInfo itemInfo = itemInfoFactory.getItemInfo(items.getKey());
                itemInfo.removeItems(items.getValue());
            }
        }
        Boolean result = userWorkspaceRepository.removeByWorkspaceId(id);
        result &= fsTreeRepository.deleteFSTree(id);
        return result;
    }

    public FSQueryWorkspaceResponseType queryWorkspaceById(FSQueryWorkspaceRequestType request) {
        FSQueryWorkspaceResponseType response = new FSQueryWorkspaceResponseType();
        FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getId());
        FSTreeType treeType = FSTreeMapper.INSTANCE.contractFromDto(treeDto);
        response.setFsTree(treeType);
        return response;
    }

    public FSQueryWorkspacesResponseType queryWorkspacesByUser(FSQueryWorkspacesRequestType request) {
        FSQueryWorkspacesResponseType response = new FSQueryWorkspacesResponseType();
        List<UserWorkspaceDto> userWorkspaceDtos = userWorkspaceRepository.queryWorkspacesByUser(request.getUserName());
        if (userWorkspaceDtos == null) {
            response.setWorkspaces(new ArrayList<>());
            return response;
        }
        Map<String, Integer> workspaceIdRoleMap = userWorkspaceDtos.stream()
                .collect(Collectors.toMap(UserWorkspaceDto::getWorkspaceId, UserWorkspaceDto::getRole));

        List<FSTreeDto> treeDtos = fsTreeRepository.queryFSTreeByIds(workspaceIdRoleMap.keySet());

        List<WorkspaceDto> workspaces = new ArrayList<>();
        treeDtos.forEach(tree -> {
            WorkspaceDto dto = new WorkspaceDto();
            dto.setId(tree.getId());
            dto.setWorkspaceName(tree.getWorkspaceName());
            dto.setRole(workspaceIdRoleMap.get(dto.getId()));
            workspaces.add(dto);
        });
        response.setWorkspaces(WorkspaceMapper.INSTANCE.contractFromDtoList(workspaces));
        return response;
    }

    public FSQueryUsersByWorkspaceResponseType queryUsersByWorkspace(FSQueryUsersByWorkspaceRequestType request) {
        FSQueryUsersByWorkspaceResponseType response = new FSQueryUsersByWorkspaceResponseType();
        List<UserWorkspaceDto> userWorkspaceDtos =
                userWorkspaceRepository.queryUsersByWorkspace(request.getWorkspaceId());
        if (userWorkspaceDtos == null) {
            response.setUsers(new ArrayList<>());
            return response;
        }
        List<UserType> users = userWorkspaceDtos.stream().map(UserWorkspaceMapper.INSTANCE::userTypeFromDto).collect(
                Collectors.toList());
        response.setUsers(users);
        return response;
    }

    public FSSaveInterfaceResponseType saveInterface(FSSaveInterfaceRequestType request) {
        FSSaveInterfaceResponseType response = new FSSaveInterfaceResponseType();
        FSInterfaceDto dto = FSInterfaceMapper.INSTANCE.dtoFromContract(request);
        try {
            fsInterfaceRepository.saveInterface(dto);
            // update method in workspace tree
            FSTreeDto workspace = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
            if (workspace != null) {
                FSNodeDto node = fileSystemUtils.deepFindByInfoId(workspace.getRoots(), request.getId());
                if (node != null) {
                    if (request.getAddress() != null && !Objects.equals(request.getAddress().getMethod(),
                            node.getMethod())) {
                        node.setMethod(request.getAddress().getMethod());
                        fsTreeRepository.updateFSTree(workspace);
                    }
                }
            }
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
        }
        return response;
    }

    public FSQueryInterfaceResponseType queryInterface(FSQueryInterfaceRequestType request) {
        FSInterfaceDto dto = fsInterfaceRepository.queryInterface(request.getId());
        if (dto == null) {
            return new FSQueryInterfaceResponseType();
        }
        return FSInterfaceMapper.INSTANCE.contractFromDto(dto);
    }

    public FSSaveCaseResponseType saveCase(FSSaveCaseRequestType request) {
        FSSaveCaseResponseType response = new FSSaveCaseResponseType();
        FSCaseDto dto = FSCaseMapper.INSTANCE.dtoFromContract(request);
        try {
            fsCaseRepository.saveCase(dto);
            // update labels in workspace tree
            FSTreeDto workspace = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
            if (workspace != null) {
                FSNodeDto node = fileSystemUtils.deepFindByInfoId(workspace.getRoots(), request.getId());
                if (node != null) {
                    if (!SetUtils.isEqualSet(node.getLabelIds(), request.getLabelIds())) {
                        node.setLabelIds(request.getLabelIds());
                        fsTreeRepository.updateFSTree(workspace);
                    }
                }
            }
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
        }
        return response;
    }

    public FSQueryCaseResponseType queryCase(FSQueryCaseRequestType request) {
        FSCaseDto dto = fsCaseRepository.queryCase(request.getId());
        if (dto == null) {
            return new FSQueryCaseResponseType();
        }

        FSQueryCaseResponseType response = FSCaseMapper.INSTANCE.contractFromDto(dto);
        return response;
    }

    public FSAddWorkspaceResponseType addWorkspace(FSAddWorkspaceRequestType request) {
        FSAddWorkspaceResponseType response = new FSAddWorkspaceResponseType();
        FSTreeDto dto = addWorkspace(request.getWorkspaceName(), request.getUserName());
        response.setWorkspaceId(dto.getId());
        return response;
    }

    public InviteToWorkspaceResponseType inviteToWorkspace(InviteToWorkspaceRequestType request) {
        InviteToWorkspaceResponseType response = new InviteToWorkspaceResponseType();
        response.setSuccessUsers(new HashSet<>());
        response.setFailedUsers(new HashSet<>());
        for (String userName : request.getUserNames()) {
            UserDto userDto = userRepository.queryUserProfile(userName);
            if (userDto == null) {
                userDto = new UserDto();
                userDto.setUserName(userName);
                userRepository.updateUserProfile(userDto);
            }

            UserWorkspaceDto userWorkspaceDto =
                    userWorkspaceRepository.queryUserWorkspace(userName, request.getWorkspaceId());
            if (userWorkspaceDto != null && userWorkspaceDto.getStatus() == InvitationType.INVITED) {
                response.getFailedUsers().add(userName);
                continue;
            }
            userWorkspaceDto = UserWorkspaceMapper.INSTANCE.dtoFromContract(request);
            userWorkspaceDto.setUserName(userName);
            userWorkspaceDto.setStatus(InvitationType.INVITING);
            userWorkspaceDto.setToken(UUID.randomUUID().toString());

            Boolean result = sendInviteEmail(request.getInvitor(),
                    userName,
                    request.getWorkspaceId(),
                    userWorkspaceDto.getToken());
            if (result) {
                userWorkspaceRepository.update(userWorkspaceDto);
                response.getSuccessUsers().add(userName);
            } else {
                response.getFailedUsers().add(userName);
            }
        }
        return response;
    }

    public Boolean leaveWorkspace(LeaveWorkspaceRequestType request) {
        return userWorkspaceRepository.remove(request.getUserName(), request.getWorkspaceId());
    }

    public ValidInvitationResponseType validInvitation(ValidInvitationRequestType request) {
        ValidInvitationResponseType response = new ValidInvitationResponseType();
        UserWorkspaceDto userWorkspaceDto = UserWorkspaceMapper.INSTANCE.dtoFromContract(request);
        Boolean result = userWorkspaceRepository.verify(userWorkspaceDto);
        if (Boolean.TRUE.equals(result)) {
            userWorkspaceDto.setStatus(InvitationType.INVITED);
            userWorkspaceRepository.update(userWorkspaceDto);
            response.setAccessToken(JwtUtil.makeAccessToken(request.getUserName()));
            response.setRefreshToken(JwtUtil.makeRefreshToken(request.getUserName()));
        }
        response.setSuccess(result);
        return response;
    }

    /**
     * @return : Tuple<workspaceId,InfoId>
     */
    public Tuple<String, String> addItemFromRecord(FSAddItemFromRecordRequestType request) {

        FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
        if (treeDto == null) {
            return null;
        }

        StorageCase.StorageCaseEntity entity = storageCase.getViewRecord(request.getRecordId());
        if (entity == null) {
            return null;
        }

        FSNodeDto parentNode = fileSystemUtils.findByPath(treeDto.getRoots(), request.getParentPath());
        if (parentNode == null) {
            return null;
        }
        if (parentNode.getNodeType() == FSInfoItem.CASE) {
            return null;
        }
        List<String> path = new ArrayList<>(Arrays.asList(request.getParentPath()));
        // add default interface if the parent path is Folder
        if (parentNode.getNodeType() == FSInfoItem.FOLDER) {
            FSAddItemRequestType addInterface = new FSAddItemRequestType();
            addInterface.setId(treeDto.getId());
            addInterface.setNodeName(DEFAULT_INTERFACE_NAME);
            addInterface.setNodeType(FSInfoItem.INTERFACE);
            addInterface.setParentPath(request.getParentPath());
            FSAddItemResponseType addItemResponse = addItem(addInterface);
            path.add(addItemResponse.getInfoId());
        }

        FSAddItemRequestType addCase = new FSAddItemRequestType();
        addCase.setId(treeDto.getId());
        addCase.setNodeName(request.getNodeName());
        addCase.setNodeType(FSInfoItem.CASE);
        addCase.setParentPath(path.toArray(new String[path.size()]));
        FSAddItemResponseType addCaseResponse = addItem(addCase);

        FSCaseDto caseDto = storageCase.getCase(path.get(path.size() - 1), addCaseResponse.getInfoId(), entity);
        fsCaseRepository.saveCase(caseDto);

        return new Tuple<>(treeDto.getId(), addCaseResponse.getInfoId());
    }

    public Tuple<Boolean, String> exportItem(FSExportItemRequestType request) {
        FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
        List<FSNodeDto> nodes;
        if (ArrayUtils.isEmpty(request.getPath())) {
            nodes = treeDto.getRoots();
        } else {
            FSNodeDto node = fileSystemUtils.findByPath(treeDto.getRoots(), request.getPath());
            nodes = Arrays.asList(node);
        }
        Map<Integer, List<String>> itemInfoIds = getItemInfoIds(nodes);
        Map<String, FSItemDto> itemInfos = getItemInfos(itemInfoIds);

        ImportExport ie = importExportFactory.getImportExport(request.getType());
        if (ie == null) {
            return new Tuple<>(false, null);
        }
        String exportString = ie.exportItem(nodes, itemInfos);
        return new Tuple<>(true, exportString);
    }

    public boolean importItem(FSImportItemRequestType request) {
        FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
        if (treeDto == null) {
            return false;
        }
        ImportExport ie = importExportFactory.getImportExport(request.getType());
        if (ie == null) {
            return false;
        }
        return ie.importItem(treeDto, request.getPath(), request.getImportString());
    }

    private Map<Integer, List<String>> getItemInfoIds(List<FSNodeDto> list) {
        Map<Integer, List<String>> typeInfoIdsMap = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_MAP;
        }
        Queue<FSNodeDto> queue = new ArrayDeque<>(list);
        while (!queue.isEmpty()) {
            FSNodeDto node = queue.poll();
            if (!typeInfoIdsMap.containsKey(node.getNodeType())) {
                typeInfoIdsMap.put(node.getNodeType(), new ArrayList<>());
            }
            typeInfoIdsMap.get(node.getNodeType()).add(node.getInfoId());
            if (CollectionUtils.isNotEmpty(node.getChildren())) {
                queue.addAll(node.getChildren());
            }
        }
        return typeInfoIdsMap;
    }

    private Map<String, FSItemDto> getItemInfos(Map<Integer, List<String>> typeInfoIdsMap) {
        Map<String, FSItemDto> result = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : typeInfoIdsMap.entrySet()) {
            ItemInfo itemInfo = itemInfoFactory.getItemInfo(entry.getKey());
            List<FSItemDto> items = itemInfo.queryByIds(entry.getValue());
            if (CollectionUtils.isNotEmpty(items)) {
                items.forEach(item -> result.put(item.getId(), item));
            }
        }
        return result;
    }

    private void removeItems(FSNodeDto fsNodeDto) {
        if (fsNodeDto == null) {
            return;
        }
        Queue<FSNodeDto> queue = new ArrayDeque<>();
        queue.add(fsNodeDto);

        while (!queue.isEmpty()) {
            FSNodeDto dto = queue.poll();
            ItemInfo itemInfo = itemInfoFactory.getItemInfo(dto.getNodeType());
            itemInfo.removeItem(dto.getInfoId());
            if (dto.getChildren() != null && dto.getChildren().size() > 0) {
                queue.addAll(dto.getChildren());
            }
        }
    }

    private FSNodeDto duplicateInfo(String parentId, String nodeName, FSNodeDto old) {
        FSNodeDto dto = new FSNodeDto();
        ItemInfo itemInfo = itemInfoFactory.getItemInfo(old.getNodeType());
        String dupInfoId = itemInfo.duplicate(parentId, old.getInfoId());
        dto.setNodeName(nodeName);
        dto.setInfoId(dupInfoId);
        dto.setMethod(old.getMethod());
        dto.setNodeType(old.getNodeType());
        if (old.getChildren() != null) {
            dto.setChildren(new ArrayList<>(old.getChildren().size()));
            for (FSNodeDto oldChild : old.getChildren()) {
                FSNodeDto dupChild = duplicateInfo(dupInfoId, oldChild.getNodeName(), oldChild);
                dto.getChildren().add(dupChild);
            }
        }
        return dto;
    }

    private Boolean sendInviteEmail(String invitor, String invitee, String workspaceId, String token) {
        FSTreeDto workspace = fsTreeRepository.queryFSTreeById(workspaceId);
        if (workspace == null) {
            return false;
        }
        final String INVITATION_MAIL_SUBJECT = "[ArexTest]You are invited to '%s' workspace";

        InviteObject inviteObject = new InviteObject(invitee, workspaceId, token);
        JSONObject obj = new JSONObject(inviteObject);

        String address = arexUiUrl + "/click/?upn=" + Base64.encode(obj.toString().getBytes());

        String context = loadResource.getResource(INVITATION_EMAIL_TEMPLATE);
        context = context.replace(SOMEBODY_PLACEHOLDER, invitor)
                .replace(WORKSPACE_NAME_PLACEHOLDER, workspace.getWorkspaceName())
                .replace(LINK_PLACEHOLDER, address);

        return mailUtils.sendEmail(invitee,
                String.format(INVITATION_MAIL_SUBJECT, workspace.getWorkspaceName()),
                context, SendEmailType.INVITATION);
    }

    private FSTreeDto addWorkspace(String workspaceName, String userName) {
        if (StringUtils.isEmpty(userName)) {
            userName = DEFAULT_WORKSPACE_NAME;
        }
        FSTreeDto dto = new FSTreeDto();
        dto.setWorkspaceName(workspaceName);
        dto.setUserName(userName);
        dto.setRoots(new ArrayList<>());
        dto = fsTreeRepository.initFSTree(dto);

        // add user workspace
        UserWorkspaceDto userWorkspaceDto = new UserWorkspaceDto();
        userWorkspaceDto.setWorkspaceId(dto.getId());
        userWorkspaceDto.setUserName(dto.getUserName());
        userWorkspaceDto.setRole(RoleType.ADMIN);
        userWorkspaceDto.setStatus(InvitationType.INVITED);
        userWorkspaceRepository.update(userWorkspaceDto);
        return dto;
    }

    @Data
    public class InviteObject {
        private String mail;
        private String workSpaceId;
        private String token;

        public InviteObject(String mail, String workSpaceId, String token) {
            this.mail = mail;
            this.workSpaceId = workSpaceId;
            this.token = token;
        }
    }
}
