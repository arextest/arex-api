package com.arextest.report.core.business.filesystem;

import com.arextest.report.common.JwtUtil;
import com.arextest.report.common.LoadResource;
import com.arextest.report.common.Tuple;
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
import com.arextest.report.model.dto.filesystem.FSNodeDto;
import com.arextest.report.model.dto.filesystem.FSTreeDto;
import com.arextest.report.model.dto.filesystem.UserWorkspaceDto;
import com.arextest.report.model.enums.FSInfoItem;
import com.arextest.report.model.enums.InvitationType;
import com.arextest.report.model.enums.RoleType;
import com.arextest.report.model.mapper.AddressMapper;
import com.arextest.report.model.mapper.FSCaseMapper;
import com.arextest.report.model.mapper.FSInterfaceMapper;
import com.arextest.report.model.mapper.FSTreeMapper;
import com.arextest.report.model.mapper.UserWorkspaceMapper;
import com.arextest.report.model.mapper.WorkspaceMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.internal.Base64;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
                infoId = itemInfo.saveItem(null, null);
                nodeDto.setInfoId(infoId);
                nodeDto.setNodeType(request.getNodeType());
                dto.getRoots().add(0, nodeDto);
            } else {
                String[] nodes = request.getParentPath();

                FSNodeDto current = findByInfoId(dto.getRoots(), nodes[0]);
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
                    current = findByInfoId(current.getChildren(), node);

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
                    infoId = itemInfo.saveItem(current.getInfoId(), current.getNodeType());
                    newNodeDto.setInfoId(infoId);
                    newNodeDto.setNodeType(request.getNodeType());
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
            FSNodeDto find = findByInfoId(current, node);

            if (find == null || find.getChildren() == null) {
                return false;
            }
            current = find.getChildren();
        }

        FSNodeDto needRemove = findByInfoId(current, nodes[nodes.length - 1]);
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
        FSNodeDto dto = findByPath(fsTreeDto.getRoots(), request.getPath());

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
                parent = findByPath(treeDto.getRoots(),
                        Arrays.copyOfRange(request.getPath(), 0, request.getPath().length - 1));
                current = findByInfoId(parent.getChildren(), request.getPath()[request.getPath().length - 1]);
            } else {
                current = findByInfoId(treeDto.getRoots(), request.getPath()[0]);
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
            FSNodeDto current = findByPath(treeDto.getRoots(), request.getFromNodePath());
            FSNodeDto fromParent = null;
            FSNodeDto toParent = null;
            if (request.getFromNodePath().length > 1) {
                fromParent = findByPath(treeDto.getRoots(),
                        Arrays.copyOfRange(request.getFromNodePath(), 0, request.getFromNodePath().length - 1));
            }
            if (request.getToParentPath() != null && request.getToParentPath().length > 0) {
                toParent = findByPath(treeDto.getRoots(), request.getToParentPath());
            }
            if (toParent == null) {
                treeDto.getRoots().add(0, current);
            } else {
                if (toParent.getChildren() == null) {
                    toParent.setChildren(new ArrayList<>());
                }
                toParent.getChildren().add(0, current);
            }
            if (fromParent == null) {
                treeDto.getRoots().remove(current);
            } else {
                fromParent.getChildren().remove(current);
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
        } catch (Exception e) {
            response.setSuccess(false);
        }
        response.setSuccess(true);
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
        } catch (Exception e) {
            response.setSuccess(false);
        }
        response.setSuccess(true);
        return response;
    }

    public FSQueryCaseResponseType queryCase(FSQueryCaseRequestType request) {
        FSCaseDto dto = fsCaseRepository.queryCase(request.getId());
        if (dto == null) {
            return new FSQueryCaseResponseType();
        }

        FSQueryCaseResponseType response = FSCaseMapper.INSTANCE.contractFromDto(dto);
        if (!StringUtils.isEmpty(dto.getParentId()) && dto.getParentNodeType() != null) {
            FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(dto.getParentId());
            response.setBaseAddress(AddressMapper.INSTANCE.contractFromDto(fsInterfaceDto.getBaseAddress()));
            response.setTestAddress(AddressMapper.INSTANCE.contractFromDto(fsInterfaceDto.getTestAddress()));
            response.setAddress(AddressMapper.INSTANCE.contractFromDto(fsInterfaceDto.getAddress()));
        }

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
            response.setAccessToken(JwtUtil.makeAccessToken(request.getUserName()));
            response.setRefreshToken(JwtUtil.makeRefreshToken(request.getUserName()));
        }
        response.setSuccess(result);
        userWorkspaceDto.setStatus(InvitationType.INVITED);
        userWorkspaceRepository.update(userWorkspaceDto);
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

        FSNodeDto parentNode = findByPath(treeDto.getRoots(), request.getParentPath());
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

    private FSNodeDto findByPath(List<FSNodeDto> list, String[] pathArr) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<FSNodeDto> tmp = list;
        for (int i = 0; i < pathArr.length - 1; i++) {
            String pathNode = pathArr[i];
            if (tmp == null || tmp.size() == 0) {
                return null;
            }
            FSNodeDto find = findByInfoId(tmp, pathNode);
            if (find == null) {
                return null;
            }
            tmp = find.getChildren();
        }
        String last = pathArr[pathArr.length - 1];
        return findByInfoId(tmp, last);
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

    private FSNodeDto findByInfoId(List<FSNodeDto> list, String infoId) {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<FSNodeDto> filter = list.stream().filter(f -> f.getInfoId().equals(infoId)).collect(Collectors.toList());
        if (filter == null || filter.size() == 0) {
            return null;
        }
        return filter.get(0);
    }

    private FSNodeDto duplicateInfo(String parentId, String nodeName, FSNodeDto old) {
        FSNodeDto dto = new FSNodeDto();
        ItemInfo itemInfo = itemInfoFactory.getItemInfo(old.getNodeType());
        String dupInfoId = itemInfo.duplicate(parentId, old.getInfoId());
        dto.setNodeName(nodeName);
        dto.setInfoId(dupInfoId);
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

        String address = "http://10.5.153.1:8088/click/?upn=" + Base64.encode(obj.toString().getBytes());

        String context = loadResource.getResource(INVITATION_EMAIL_TEMPLATE);
        context = context.replace(SOMEBODY_PLACEHOLDER, invitor)
                .replace(WORKSPACE_NAME_PLACEHOLDER, workspace.getWorkspaceName())
                .replace(LINK_PLACEHOLDER, address);

        return mailUtils.sendEmail(invitee,
                String.format(INVITATION_MAIL_SUBJECT, workspace.getWorkspaceName()),
                context,
                true);
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
