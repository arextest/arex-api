package com.arextest.web.core.business.filesystem;

import com.arextest.web.common.JwtUtil;
import com.arextest.web.common.LoadResource;
import com.arextest.web.common.LogUtils;
import com.arextest.web.common.Tuple;
import com.arextest.web.core.business.filesystem.importexport.ImportExport;
import com.arextest.web.core.business.filesystem.importexport.impl.ImportExportFactory;
import com.arextest.web.core.business.filesystem.pincase.StorageCase;
import com.arextest.web.core.business.filesystem.recovery.RecoveryFactory;
import com.arextest.web.core.business.filesystem.recovery.RecoveryService;
import com.arextest.web.core.business.util.MailUtils;
import com.arextest.web.core.repository.FSCaseRepository;
import com.arextest.web.core.repository.FSFolderRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.FSTraceLogRepository;
import com.arextest.web.core.repository.FSTreeRepository;
import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.core.repository.UserRepository;
import com.arextest.web.core.repository.UserWorkspaceRepository;
import com.arextest.web.model.contract.contracts.filesystem.ChangeRoleRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemFromRecordRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSDuplicateRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSExportItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSImportItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSMoveItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSPinMockRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryCaseRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryCaseResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryFolderRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryFolderResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryInterfaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryInterfaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryUsersByWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryUsersByWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryWorkspacesRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryWorkspacesResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSRemoveItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSRenameRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSRenameWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveFolderRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveFolderResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveInterfaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSTreeType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.RecoverItemInfoRequestType;
import com.arextest.web.model.contract.contracts.filesystem.UserType;
import com.arextest.web.model.contract.contracts.filesystem.ValidInvitationRequestType;
import com.arextest.web.model.contract.contracts.filesystem.ValidInvitationResponseType;
import com.arextest.web.model.dto.KeyValuePairDto;
import com.arextest.web.model.dto.ReportPlanStatisticDto;
import com.arextest.web.model.dto.UserDto;
import com.arextest.web.model.dto.WorkspaceDto;
import com.arextest.web.model.dto.filesystem.AddressDto;
import com.arextest.web.model.dto.filesystem.FSCaseDto;
import com.arextest.web.model.dto.filesystem.FSFolderDto;
import com.arextest.web.model.dto.filesystem.FSInterfaceAndCaseBaseDto;
import com.arextest.web.model.dto.filesystem.FSInterfaceDto;
import com.arextest.web.model.dto.filesystem.FSItemDto;
import com.arextest.web.model.dto.filesystem.FSNodeDto;
import com.arextest.web.model.dto.filesystem.FSTraceLogDto;
import com.arextest.web.model.dto.filesystem.FSTreeDto;
import com.arextest.web.model.dto.filesystem.UserWorkspaceDto;
import com.arextest.web.model.enums.CaseSourceType;
import com.arextest.web.model.enums.FSInfoItem;
import com.arextest.web.model.enums.InvitationType;
import com.arextest.web.model.enums.RoleType;
import com.arextest.web.model.enums.SendEmailType;
import com.arextest.web.model.mapper.FSCaseMapper;
import com.arextest.web.model.mapper.FSFolderMapper;
import com.arextest.web.model.mapper.FSInterfaceMapper;
import com.arextest.web.model.mapper.FSTreeMapper;
import com.arextest.web.model.mapper.UserWorkspaceMapper;
import com.arextest.web.model.mapper.WorkspaceMapper;
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
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
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
    private static final String AREX_RECORD_ID = "arex-record-id";

    @Value("${arex.ui.url}")
    private String arexUiUrl;

    @Value("${arex.report.case.inherited}")
    private String arexCaseInherited;

    @Resource
    private FSTreeRepository fsTreeRepository;

    @Resource
    private FSFolderRepository fsFolderRepository;

    @Resource
    private FSInterfaceRepository fsInterfaceRepository;

    @Resource
    private FSCaseRepository fsCaseRepository;

    @Resource
    private UserWorkspaceRepository userWorkspaceRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private FSTraceLogRepository fsTraceLogRepository;

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

    @Resource
    private FSTraceLogUtils fsTraceLogUtils;

    @Resource
    private RecoveryFactory recoveryFactory;

    @Resource
    private ReportPlanStatisticRepository reportPlanStatisticRepository;


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


            String workspaceId = null;
            if (StringUtils.isEmpty(request.getId())) {
                FSTreeDto dto = addWorkspace(request.getWorkspaceName(), request.getUserName());
                workspaceId = dto.getId();
            } else {
                workspaceId = request.getId();
            }

            AtomicReference<String> infoId = new AtomicReference<>();

            FSTreeDto workspace = fsTreeRepository.updateFSTree(workspaceId, dto -> {
                List<FSNodeDto> targetTreeNodes = null;
                if (request.getParentPath() == null || request.getParentPath().length == 0) {
                    infoId.set(itemInfo.initItem(null, null, dto.getId(), request.getNodeName()));
                    targetTreeNodes = dto.getRoots();
                } else {
                    String[] nodes = request.getParentPath();

                    FSNodeDto current = fileSystemUtils.findByInfoId(dto.getRoots(), nodes[0]);
                    if (current == null) {
                        return null;
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
                    if (error) {
                        return null;
                    }

                    if (current.getChildren() == null) {
                        current.setChildren(new ArrayList<>());
                    }
                    infoId.set(itemInfo.initItem(current.getInfoId(),
                            current.getNodeType(),
                            dto.getId(),
                            request.getNodeName()));
                    targetTreeNodes = current.getChildren();
                }

                FSNodeDto nodeDto = new FSNodeDto();
                nodeDto.setNodeName(request.getNodeName());
                nodeDto.setInfoId(infoId.get());
                nodeDto.setNodeType(request.getNodeType());
                if (request.getNodeType() == FSInfoItem.CASE) {
                    nodeDto.setCaseSourceType(request.getCaseSourceType());
                }
                if (request.getNodeType() == FSInfoItem.INTERFACE) {
                    nodeDto.setMethod(GET_METHOD);
                }
                targetTreeNodes.add(0, nodeDto);
                return dto;
            });
            if (workspace != null) {
                response.setSuccess(true);
                response.setInfoId(infoId.get());
                response.setWorkspaceId(workspaceId);
            } else {
                response.setSuccess(false);
            }

        } catch (Exception e) {
            LogUtils.error(LOGGER, "failed to add item to filesystem", e);
            response.setSuccess(false);
        }
        return response;
    }

    public Boolean removeItem(FSRemoveItemRequestType request, String userName) {

        FSTreeDto treeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {
            if (dto == null) {
                return null;
            }
            List<FSNodeDto> current = dto.getRoots();
            if (current == null) {
                return null;
            }

            String parentId = null;
            String[] nodes = request.getRemoveNodePath();
            for (int i = 0; i < nodes.length - 1; i++) {

                String node = nodes[i];
                FSNodeDto find = fileSystemUtils.findByInfoId(current, node);

                if (find == null || find.getChildren() == null) {
                    return null;
                }
                current = find.getChildren();
                parentId = find.getInfoId();
            }

            FSNodeDto needRemove = fileSystemUtils.findByInfoId(current, nodes[nodes.length - 1]);
            removeItems(needRemove, userName, parentId, request.getId());
            current.remove(needRemove);
            return dto;
        });

        return treeDto != null ? true : false;
    }

    public Boolean rename(FSRenameRequestType request) {

        FSTreeDto fsTreeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {
            if (dto == null) {
                return null;
            }
            FSNodeDto fsNodeDto = fileSystemUtils.findByPath(dto.getRoots(), request.getPath());
            if (fsNodeDto == null) {
                return null;
            }
            ItemInfo itemInfo = itemInfoFactory.getItemInfo(fsNodeDto.getNodeType());
            FSItemDto itemDto = itemInfo.queryById(request.getPath()[request.getPath().length - 1]);
            if (itemInfo == null) {
                return null;
            }
            itemDto.setName(request.getNewName());
            itemInfo.saveItem(itemDto);
            fsNodeDto.setNodeName(request.getNewName());
            return dto;
        });
        return fsTreeDto != null ? true : false;
    }

    public Boolean duplicate(FSDuplicateRequestType request) {
        try {
            FSTreeDto treeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {
                FSNodeDto parent = null;
                FSNodeDto current;
                if (request.getPath().length != 1) {
                    parent = fileSystemUtils.findByPath(dto.getRoots(),
                            Arrays.copyOfRange(request.getPath(), 0, request.getPath().length - 1));
                    current = fileSystemUtils.findByInfoId(parent.getChildren(),
                            request.getPath()[request.getPath().length - 1]);
                } else {
                    current = fileSystemUtils.findByInfoId(dto.getRoots(), request.getPath()[0]);
                }
                FSNodeDto dupNodeDto = duplicateInfo(parent == null ? null : parent.getInfoId(),
                        current.getNodeName() + DUPLICATE_SUFFIX,
                        current);
                if (parent == null) {
                    this.addDuplicateItemFollowCurrent(dto.getRoots(), dupNodeDto, current);
                } else {
                    this.addDuplicateItemFollowCurrent(parent.getChildren(), dupNodeDto, current);
                }
                return dto;
            });

            return treeDto != null ? true : false;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "failed to duplicate item", e);
            return false;
        }
    }

    public Boolean move(FSMoveItemRequestType request) {
        try {
            FSTreeDto treeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {
                Tuple<Integer, FSNodeDto> current =
                        fileSystemUtils.findByPathWithIndex(dto.getRoots(), request.getFromNodePath());
                if (current == null) {
                    return null;
                }
                FSNodeDto fromParent = null;
                FSNodeDto toParent = null;
                if (request.getFromNodePath().length > 1) {
                    fromParent = fileSystemUtils.findByPath(dto.getRoots(),
                            Arrays.copyOfRange(request.getFromNodePath(), 0, request.getFromNodePath().length - 1));
                }
                if (request.getToParentPath() != null && request.getToParentPath().length > 0) {
                    toParent = fileSystemUtils.findByPath(dto.getRoots(), request.getToParentPath());
                }
                Integer toIndex = request.getToIndex() == null ? 0 : request.getToIndex();
                if (toParent == null) {
                    dto.getRoots().add(toIndex, current.y);
                    updateParentId(current.y, "");
                } else {
                    if (toParent.getChildren() == null) {
                        toParent.setChildren(new ArrayList<>());
                    }
                    toParent.getChildren().add(toIndex, current.y);
                    updateParentId(current.y, request.getToParentPath()[request.getToParentPath().length - 1]);
                }
                if (fromParent == null && toParent == null) {
                    if (request.getToIndex() < current.x) {
                        dto.getRoots().remove(current.x + 1);
                    } else {
                        dto.getRoots().remove(current.x.intValue());
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
                        dto.getRoots().remove(current.x.intValue());
                    } else {
                        fromParent.getChildren().remove(current.x.intValue());
                    }
                }
                return dto;
            });
            return treeDto != null ? true : false;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "failed to move item", e);
            return false;
        }
    }

    public Boolean renameWorkspace(FSRenameWorkspaceRequestType request) {
        try {
            FSTreeDto treeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {
                dto.setWorkspaceName(request.getWorkspaceName());
                return dto;
            });
            return treeDto != null ? true : false;
        } catch (Exception e) {
            LogUtils.error(LOGGER, "failed to rename workspace", e);
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

    public FSSaveFolderResponseType saveFolder(FSSaveFolderRequestType request, String userName) {
        FSSaveFolderResponseType response = new FSSaveFolderResponseType();
        FSFolderDto dto = FSFolderMapper.INSTANCE.dtoFromContract(request);

        try {
            dto = fsFolderRepository.saveFolder(dto);
            fsTraceLogUtils.logUpdateItem(userName, dto);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
        }
        return response;
    }

    public FSQueryFolderResponseType queryFolder(FSQueryFolderRequestType request) {
        FSFolderDto dto = fsFolderRepository.queryById(request.getId());
        if (dto == null) {
            return new FSQueryFolderResponseType();
        }
        return FSFolderMapper.INSTANCE.contractFromDto(dto);
    }

    public boolean saveInterface(FSSaveInterfaceRequestType request, String userName) {
        FSInterfaceDto interfaceDto = FSInterfaceMapper.INSTANCE.dtoFromContract(request);
        try {
            interfaceDto = fsInterfaceRepository.saveInterface(interfaceDto);
            fsTraceLogUtils.logUpdateItem(userName, interfaceDto);
            // update method in workspace tree
            fsTreeRepository.updateFSTree(request.getWorkspaceId(), dto -> {
                if (dto == null) {
                    return null;
                }
                FSNodeDto node = fileSystemUtils.deepFindByInfoId(dto.getRoots(), request.getId());
                if (node == null) {
                    return null;
                }
                if (request.getAddress() != null && !Objects.equals(request.getAddress().getMethod(),
                        node.getMethod())) {
                    node.setMethod(request.getAddress().getMethod());
                    return dto;
                }
                return null;
            });
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to save interface", e);
            return false;
        }
    }

    public FSQueryInterfaceResponseType queryInterface(FSQueryInterfaceRequestType request) {
        FSInterfaceDto dto = fsInterfaceRepository.queryInterface(request.getId());
        if (dto == null) {
            return new FSQueryInterfaceResponseType();
        }
        return FSInterfaceMapper.INSTANCE.contractFromDto(dto);
    }

    public boolean saveCase(FSSaveCaseRequestType request, String userName) {
        FSCaseDto caseDto = FSCaseMapper.INSTANCE.dtoFromContract(request);
        try {
            caseDto = fsCaseRepository.saveCase(caseDto);
            fsTraceLogUtils.logUpdateItem(userName, caseDto);
            // update labels in workspace tree
            fsTreeRepository.updateFSTree(request.getWorkspaceId(), dto -> {
                if (dto == null) {
                    return null;
                }
                FSNodeDto node = fileSystemUtils.deepFindByInfoId(dto.getRoots(), request.getId());
                if (node == null) {
                    return null;
                }
                if (!SetUtils.isEqualSet(node.getLabelIds(), request.getLabelIds())) {
                    node.setLabelIds(request.getLabelIds());
                    return dto;
                }
                return null;
            });

            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to save case", e);
            return false;
        }
    }

    public FSQueryCaseResponseType queryCase(FSQueryCaseRequestType request) {
        FSCaseDto dto = fsCaseRepository.queryCase(request.getId(), request.isGetCompareMsg());
        if (dto == null) {
            return new FSQueryCaseResponseType();
        }
        if (dto.getInherited() == null) {
            dto.setInherited("true".equalsIgnoreCase(arexCaseInherited) ? Boolean.TRUE : Boolean.FALSE);
        }

        FSQueryCaseResponseType response = FSCaseMapper.INSTANCE.contractFromDto(dto);
        String parentId = dto.getParentId();
        FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(parentId);
        if (fsInterfaceDto != null) {
            FSQueryInterfaceResponseType fsQueryInterfaceResponseType =
                    FSInterfaceMapper.INSTANCE.contractFromDto(fsInterfaceDto);
            response.setParentPreRequestScripts(fsQueryInterfaceResponseType.getPreRequestScripts());
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

    public boolean leaveWorkspace(String userName, String workspaceId) {
        return userWorkspaceRepository.remove(userName, workspaceId);
    }

    public boolean changeRole(ChangeRoleRequestType request) {
        UserWorkspaceDto dto = UserWorkspaceMapper.INSTANCE.dtoFromContract(request);
        try {
            userWorkspaceRepository.update(dto);
            return true;
        } catch (Exception e) {
            return false;
        }
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

        FSCaseDto caseDto = storageCase.getViewRecord(request.getRecordId());
        if (caseDto == null) {
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

        // add the related information about the replay interface to the manual interface
        this.addReplayInfoToManual(request.getOperationId(), path);

        FSAddItemRequestType addCase = new FSAddItemRequestType();
        addCase.setId(treeDto.getId());
        addCase.setNodeName(request.getNodeName());
        addCase.setNodeType(FSInfoItem.CASE);
        addCase.setParentPath(path.toArray(new String[path.size()]));
        addCase.setCaseSourceType(CaseSourceType.REPLAY_CASE);
        FSAddItemResponseType addCaseResponse = addItem(addCase);

        caseDto.setParentId(path.get(path.size() - 1));
        caseDto.setId(addCaseResponse.getInfoId());
        String newRecordId = storageCase.getNewRecordId(request.getRecordId());
        caseDto.setRecordId(newRecordId);

        KeyValuePairDto recordHeader = new KeyValuePairDto();
        recordHeader.setKey(AREX_RECORD_ID);
        recordHeader.setValue(newRecordId);
        recordHeader.setActive(true);
        caseDto.getHeaders().removeIf(item -> Objects.equals(item.getKey(), AREX_RECORD_ID));
        caseDto.getHeaders().add(0, recordHeader);

        // modify the address of fsCase(domain + request path)
        String planId = request.getPlanId();
        ReportPlanStatisticDto reportPlanStatisticDto = reportPlanStatisticRepository.findByPlanId(planId);
        AddressDto address = caseDto.getAddress();
        if (address != null) {
            address.setEndpoint(this.contactUrl(reportPlanStatisticDto.getTargetEnv(),
                    address.getEndpoint()));
        }

        // when fix the case form replay, don't inherit the address of the parent interface
        caseDto.setInherited(false);

        if (!storageCase.pinnedCase(request.getRecordId(), newRecordId)) {
            return null;
        }

        fsCaseRepository.saveCase(caseDto);
        return new Tuple<>(treeDto.getId(), addCaseResponse.getInfoId());
    }

    public boolean pinMock(FSPinMockRequestType request) {
        if (request.getNodeType() == FSInfoItem.FOLDER) {
            LogUtils.error(LOGGER, "Not support NodeType:{} in pinMock operation", request.getNodeType());
            return false;
        }
        String newRecordId = storageCase.getNewRecordId(request.getRecordId());
        boolean success = storageCase.pinnedCase(request.getRecordId(), newRecordId);
        if (!success) {
            LogUtils.error(LOGGER, "Pin Case failed.recordId:{}", request.getRecordId());
            return false;
        }
        ItemInfo itemInfo = itemInfoFactory.getItemInfo(request.getNodeType());
        if (itemInfo == null) {
            return false;
        }
        FSItemDto itemDto = itemInfo.queryById(request.getInfoId());
        FSInterfaceAndCaseBaseDto interfaceDto = (FSInterfaceAndCaseBaseDto) itemDto;
        interfaceDto.setRecordId(newRecordId);
        if (interfaceDto.getHeaders() == null) {
            interfaceDto.setHeaders(new ArrayList<>());
        }
        KeyValuePairDto kvDto = new KeyValuePairDto();
        kvDto.setKey(AREX_RECORD_ID);
        kvDto.setValue(newRecordId);
        kvDto.setActive(true);
        interfaceDto.getHeaders().add(0, kvDto);

        itemInfo.saveItem(itemDto);

        // update tree
        if (request.getNodeType() == FSInfoItem.CASE) {

            fsTreeRepository.updateFSTree(request.getWorkspaceId(), dto -> {
                if (dto == null) {
                    return null;
                }
                FSNodeDto node = fileSystemUtils.deepFindByInfoId(
                        dto.getRoots(), request.getInfoId()
                );
                if (node != null) {
                    node.setCaseSourceType(CaseSourceType.REPLAY_CASE);
                    return dto;
                }
                return null;
            });
        }

        return true;
    }

    public boolean recovery(RecoverItemInfoRequestType request) {
        FSTraceLogDto traceLogDto = fsTraceLogRepository.queryTraceLog(request.getRecoveryId());
        RecoveryService recoveryService = recoveryFactory.getRecoveryService(traceLogDto.getTraceType());
        return recoveryService.recovery(traceLogDto);
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

    private Map<Integer, Set<String>> removeItems(FSNodeDto fsNodeDto,
                                                  String userName,
                                                  String parentId,
                                                  String workspaceId) {
        if (fsNodeDto == null) {
            return null;
        }
        Queue<FSNodeDto> queue = new ArrayDeque<>();
        queue.add(fsNodeDto);
        Map<Integer, Set<String>> itemInfoIds = new HashMap<>();

        while (!queue.isEmpty()) {
            FSNodeDto dto = queue.poll();
            if (dto.getChildren() != null && dto.getChildren().size() > 0) {
                queue.addAll(dto.getChildren());
            }
            if (!itemInfoIds.containsKey(dto.getNodeType())) {
                itemInfoIds.put(dto.getNodeType(), new HashSet<>());
            }
            itemInfoIds.get(dto.getNodeType()).add(dto.getInfoId());
        }
        List<FSItemDto> items = new ArrayList<>();
        for (Map.Entry<Integer, Set<String>> ids : itemInfoIds.entrySet()) {
            ItemInfo itemInfo = itemInfoFactory.getItemInfo(ids.getKey());
            items.addAll(itemInfo.queryByIds(new ArrayList<>(ids.getValue())));
            itemInfo.removeItems(ids.getValue());
        }

        fsTraceLogUtils.logDeleteItem(userName,
                workspaceId,
                fsNodeDto.getInfoId(),
                parentId,
                items,
                fsNodeDto);
        return itemInfoIds;
    }

    private FSNodeDto duplicateInfo(String parentId, String nodeName, FSNodeDto old) {
        FSNodeDto dto = new FSNodeDto();
        ItemInfo itemInfo = itemInfoFactory.getItemInfo(old.getNodeType());
        String dupInfoId = itemInfo.duplicate(parentId, old.getInfoId(), nodeName);
        dto.setNodeName(nodeName);
        dto.setInfoId(dupInfoId);
        dto.setMethod(old.getMethod());
        dto.setNodeType(old.getNodeType());
        dto.setLabelIds(old.getLabelIds());
        if (old.getNodeType() == FSInfoItem.CASE) {
            dto.setCaseSourceType(old.getCaseSourceType());
        }
        if (old.getChildren() != null) {
            dto.setChildren(new ArrayList<>(old.getChildren().size()));
            for (FSNodeDto oldChild : old.getChildren()) {
                FSNodeDto dupChild = duplicateInfo(dupInfoId, oldChild.getNodeName(), oldChild);
                dto.getChildren().add(dupChild);
            }
        }
        return dto;
    }

    private void updateParentId(FSNodeDto fsNodeDto, String parentId) {
        if (fsNodeDto == null) {
            return;
        }
        ItemInfo itemInfo = itemInfoFactory.getItemInfo(fsNodeDto.getNodeType());
        FSItemDto fsItemDto = itemInfo.queryById(fsNodeDto.getInfoId());
        if (fsItemDto != null) {
            fsItemDto.setParentId(parentId);
            itemInfo.saveItem(fsItemDto);
        }
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

    private void addReplayInfoToManual(String operationId, List<String> path) {
        if (CollectionUtils.isNotEmpty(path)) {
            FSInterfaceDto fsInterfaceDto = new FSInterfaceDto();
            fsInterfaceDto.setId(path.get(path.size() - 1));
            fsInterfaceDto.setOperationId(operationId);
            fsInterfaceRepository.saveInterface(fsInterfaceDto);
        }
    }

    private void addDuplicateItemFollowCurrent(List<FSNodeDto> treeDtos, FSNodeDto dupNodeDto, FSNodeDto currentDto) {
        int size = treeDtos.size();
        int targetIndex = size;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(treeDtos.get(i).getInfoId(), currentDto.getInfoId())) {
                targetIndex = i + 1;
                break;
            }
        }
        treeDtos.add(targetIndex, dupNodeDto);
    }

    private String contactUrl(String domain, String operation) {
        String result = null;
        domain = Optional.ofNullable(domain).orElse(StringUtils.EMPTY);
        operation = Optional.ofNullable(operation).orElse(StringUtils.EMPTY);
        boolean domainContain = StringUtils.endsWith(domain, "/");
        boolean operationContain = StringUtils.startsWith(operation, "/");
        if (domainContain && operationContain) {
            result = domain + operation.substring(1);
        } else if (!domainContain && !operationContain) {
            result = domain + "/" + operation;
        } else {
            result = domain + operation;
        }
        return result;
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
