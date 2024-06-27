package com.arextest.web.core.business.filesystem;

import com.arextest.common.exceptions.ArexException;
import com.arextest.common.jwt.JWTService;
import com.arextest.config.repository.ConfigRepositoryProvider;
import com.arextest.web.common.LoadResource;
import com.arextest.web.common.LogUtils;
import com.arextest.web.common.ZstdUtils;
import com.arextest.web.common.exception.ArexApiResponseCode;
import com.arextest.web.core.business.beans.MailService;
import com.arextest.web.core.business.filesystem.importexport.ImportExport;
import com.arextest.web.core.business.filesystem.importexport.impl.ImportExportFactory;
import com.arextest.web.core.business.filesystem.pincase.StorageCase;
import com.arextest.web.core.business.filesystem.recovery.RecoveryFactory;
import com.arextest.web.core.business.filesystem.recovery.RecoveryService;
import com.arextest.web.core.business.util.JsonUtils;
import com.arextest.web.core.repository.FSCaseRepository;
import com.arextest.web.core.repository.FSFolderRepository;
import com.arextest.web.core.repository.FSInterfaceRepository;
import com.arextest.web.core.repository.FSTraceLogRepository;
import com.arextest.web.core.repository.FSTreeRepository;
import com.arextest.web.core.repository.ReportPlanStatisticRepository;
import com.arextest.web.core.repository.UserRepository;
import com.arextest.web.core.repository.UserWorkspaceRepository;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import com.arextest.web.model.contract.contracts.filesystem.BatchGetInterfaceCaseRequestType;
import com.arextest.web.model.contract.contracts.filesystem.BatchGetInterfaceCaseResponseType;
import com.arextest.web.model.contract.contracts.filesystem.ChangeRoleRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemsByAppAndInterfaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSDuplicateRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSDuplicateResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSExportItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetPathInfoResponseType.FSPathInfoDto;
import com.arextest.web.model.contract.contracts.filesystem.FSGetWorkspaceItemTreeRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetWorkspaceItemTreeResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetWorkspaceItemsRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetWorkspaceItemsResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSImportItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSMoveItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSNodeType;
import com.arextest.web.model.contract.contracts.filesystem.FSPinMockRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryCaseRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryCaseResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryFolderRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryFolderResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryInterfaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryInterfaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSQueryItemType;
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
import com.arextest.web.model.contract.contracts.filesystem.FSSearchWorkspaceItemsRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSearchWorkspaceItemsResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSTreeType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.LabelType;
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
import com.arextest.web.model.mapper.FSNodeMapper;
import com.arextest.web.model.mapper.FSTreeMapper;
import com.arextest.web.model.mapper.UserWorkspaceMapper;
import com.arextest.web.model.mapper.WorkspaceMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileSystemService {

  private static final String DEFAULT_WORKSPACE_NAME = "MyWorkSpace";
  private static final String DUPLICATE_SUFFIX = "_copy";
  private static final String INVITATION_EMAIL_TEMPLATE = "classpath:invitationEmailTemplate.htm";
  private static final String SOMEBODY_PLACEHOLDER = "{{somebody}}";
  private static final String WORKSPACE_NAME_PLACEHOLDER = "{{workspaceName}}";
  private static final String LINK_PLACEHOLDER = "{{link}}";
  private static final String GET_METHOD = "GET";
  private static final String AREX_RECORD_ID = "arex-record-id";
  private static final String AREX_REPLAY_PREPARE_DEPENDENCY = "arex_replay_prepare_dependency";
  private static final String PINNED_PRE_FIX = "pinned_";
  private static final String LOCAL_HOST = "http://127.0.0.1";
  private static final String DEFAULT_PORT = "8080";
  private static final String HOST_KEY = "host";
  private static final String COLON = ":";
  private static final String NULL_PLAN_ID = "undefined";
  private static final String EQUALS = "=";
  private static final String NOT_EQUALS = "!=";
  private static final String SKIP_MOCK_HEADER = "X-AREX-Exclusion-Operations";

  @Value("${arex.api.case.inherited}")
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
  private MailService mailService;

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

  @Resource
  private ObjectMapper objectMapper;

  @Resource
  private JWTService jwtService;

  @Resource(name = "custom-fork-join-executor")
  private ThreadPoolTaskExecutor customForkJoinExecutor;

  @Resource
  private ConfigRepositoryProvider<ScheduleConfiguration> scheduleConfigurationProvider;

  public FSAddItemResponseType addItemForController(FSAddItemRequestType request) {
    FSAddItemResponseType response = new FSAddItemResponseType();
    MutablePair<String, FSTreeDto> tuple = addItem(request);
    if (tuple == null) {
      response.setSuccess(false);
      return response;
    }
    response.setSuccess(true);
    response.setInfoId(tuple.getLeft());
    response.setWorkspaceId(tuple.getRight().getId());
    return response;
  }

  /**
   * add item into workspace
   *
   * @return <InfoId, FSTreeDto>
   */
  public MutablePair<String, FSTreeDto> addItem(FSAddItemRequestType request) {

    ItemInfo itemInfo = itemInfoFactory.getItemInfo(request.getNodeType());
    if (itemInfo == null) {
      return null;
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
        List<String> parentPath = request.getParentPath();
        if (CollectionUtils.isEmpty(parentPath)) {
          infoId.set(itemInfo.initItem(null, null, dto.getId(), request.getNodeName()));
          targetTreeNodes = dto.getRoots();
        } else {

          FSNodeDto current = fileSystemUtils.findByInfoId(dto.getRoots(), parentPath.get(0));
          if (current == null) {
            return null;
          }

          boolean error = false;
          for (int i = 1; i < parentPath.size(); i++) {
            String node = parentPath.get(i);
            if (current.getChildren() == null || current.getChildren().isEmpty()) {
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
          infoId.set(itemInfo.initItem(current.getInfoId(), current.getNodeType(), dto.getId(),
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
        return new MutablePair<>(infoId.get(), workspace);
      }
    } catch (Exception e) {
      LogUtils.error(LOGGER, "failed to add item to filesystem", e);
      return null;
    }
    return null;
  }

  /**
   * add item into workspace
   *
   * @return <Success, Paths>
   */
  public Boolean removeItem(FSRemoveItemRequestType request,
      String userName) {
    List<String> path = request.getRemoveNodePath();
    FSTreeDto treeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {
      if (dto == null) {
        return null;
      }
      List<FSNodeDto> current = dto.getRoots();
      if (current == null) {
        return null;
      }

      String parentId = null;
      for (int i = 0; i < path.size() - 1; i++) {

        String node = path.get(i);
        FSNodeDto find = fileSystemUtils.findByInfoId(current, node);

        if (find == null || find.getChildren() == null) {
          return null;
        }
        current = find.getChildren();
        parentId = find.getInfoId();
      }

      FSNodeDto needRemove = fileSystemUtils.findByInfoId(current, path.get(path.size() - 1));
      removeItems(needRemove, userName, parentId, request.getId());
      current.remove(needRemove);
      return dto;
    });

    return treeDto != null;
  }

  public Boolean rename(FSRenameRequestType request) {

    List<String> path = request.getPath();
    FSTreeDto fsTreeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {
      if (dto == null) {
        return null;
      }

      FSNodeDto fsNodeDto = fileSystemUtils.findByPath(dto.getRoots(), path);
      if (fsNodeDto == null) {
        return null;
      }
      ItemInfo itemInfo = itemInfoFactory.getItemInfo(fsNodeDto.getNodeType());
      FSItemDto itemDto = itemInfo.queryById(path.get(path.size() - 1));
      if (itemDto == null) {
        return null;
      }
      itemDto.setName(request.getNewName());
      itemInfo.saveItem(itemDto);
      fsNodeDto.setNodeName(request.getNewName());
      return dto;
    });
    return fsTreeDto != null;
  }

  public FSDuplicateResponseType duplicate(FSDuplicateRequestType request) {
    try {
      AtomicReference<String> infoId = new AtomicReference<>();
      List<String> path = request.getPath();
      FSTreeDto treeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {
        FSNodeDto parent = null;
        FSNodeDto current;
        if (path.size() != 1) {
          parent = fileSystemUtils.findByPath(dto.getRoots(), path.subList(0, path.size() - 1));
          current = fileSystemUtils.findByInfoId(parent.getChildren(),
              path.get(path.size() - 1));
        } else {
          current = fileSystemUtils.findByInfoId(dto.getRoots(), path.get(0));
        }
        FSNodeDto dupNodeDto = duplicateInfo(parent == null ? null : parent.getInfoId(),
            current.getNodeName() + DUPLICATE_SUFFIX, current);
        infoId.set(dupNodeDto.getInfoId());
        if (parent == null) {
          this.addDuplicateItemFollowCurrent(dto.getRoots(), dupNodeDto, current);
        } else {
          this.addDuplicateItemFollowCurrent(parent.getChildren(), dupNodeDto, current);
        }
        return dto;
      });
      FSDuplicateResponseType response = new FSDuplicateResponseType();
      response.setSuccess(true);
      response.setInfoId(infoId.get());
      response.setWorkspaceId(treeDto.getId());
      return response;
    } catch (Exception e) {
      throw new ArexException(ArexApiResponseCode.FS_DUPLICATE_ITEM_ERROR,
          "failed to duplicate item", e);
    }
  }

  public Boolean move(FSMoveItemRequestType request) {
    try {
      List<String> fromPath = request.getFromNodePath();
      FSTreeDto treeDto = fsTreeRepository.updateFSTree(request.getId(), dto -> {

        List<String> toParentPath = request.getToParentPath();
        MutablePair<Integer, FSNodeDto> current =
            fileSystemUtils.findByPathWithIndex(dto.getRoots(), fromPath);
        if (current == null) {
          return null;
        }
        FSNodeDto fromParent = null;
        FSNodeDto toParent = null;
        if (fromPath.size() > 1) {
          fromParent = fileSystemUtils.findByPath(dto.getRoots(),
              fromPath.subList(0, fromPath.size() - 1));
        }
        if (CollectionUtils.isNotEmpty(toParentPath)) {
          toParent = fileSystemUtils.findByPath(dto.getRoots(), toParentPath);
        }
        Integer toIndex = request.getToIndex() == null ? 0 : request.getToIndex();

        if (fromParent == null) {
          dto.getRoots().remove(current.getLeft().intValue());
        } else {
          fromParent.getChildren().remove(current.getLeft().intValue());
        }

        if (toParent == null) {
          dto.getRoots().add(toIndex, current.getRight());
          updateParentId(current.getRight(), "", 0);
        } else {
          if (toParent.getChildren() == null) {
            toParent.setChildren(new ArrayList<>());
          }
          toParent.getChildren().add(toIndex, current.getRight());
          updateParentId(current.getRight(), toParent.getInfoId(), toParent.getNodeType());
        }
        return dto;
      });
      return treeDto != null;

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
    List<UserWorkspaceDto> userWorkspaceDtos = userWorkspaceRepository.queryWorkspacesByUser(
        request.getUserName());
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

  public FSQueryUsersByWorkspaceResponseType queryUsersByWorkspace(
      FSQueryUsersByWorkspaceRequestType request) {
    FSQueryUsersByWorkspaceResponseType response = new FSQueryUsersByWorkspaceResponseType();
    List<UserWorkspaceDto> userWorkspaceDtos =
        userWorkspaceRepository.queryUsersByWorkspace(request.getWorkspaceId());
    if (userWorkspaceDtos == null) {
      response.setUsers(new ArrayList<>());
      return response;
    }
    List<UserType> users =
        userWorkspaceDtos.stream().map(UserWorkspaceMapper.INSTANCE::userTypeFromDto)
            .collect(Collectors.toList());
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
    FSQueryFolderResponseType response = FSFolderMapper.INSTANCE.contractFromDto(dto);
    response.setParentPath(getParentPath(dto.getParentId(), dto.getParentNodeType()));
    return response;
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
        boolean isChanged = false;
        if (request.getAddress() != null
            && !Objects.equals(request.getAddress().getMethod(), node.getMethod())) {
          node.setMethod(request.getAddress().getMethod());
          isChanged = true;
        }
        if (!SetUtils.isEqualSet(node.getLabelIds(), request.getLabelIds())) {
          node.setLabelIds(request.getLabelIds());
          isChanged = true;
        }
        if (isChanged) {
          return dto;
        } else {
          return null;
        }
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
    FSQueryInterfaceResponseType response = FSInterfaceMapper.INSTANCE.contractFromDto(dto);
    response.setParentPath(getParentPath(dto.getParentId(), dto.getParentNodeType()));
    return response;
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
    response.setParentPath(getParentPath(dto.getParentId(), dto.getParentNodeType()));
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

      Boolean result = sendInviteEmail(request.getArexUiUrl(), request.getInvitor(), userName,
          request.getWorkspaceId(), userWorkspaceDto.getToken());
      if (Boolean.TRUE.equals(result)) {
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
      response.setAccessToken(jwtService.makeAccessToken(request.getUserName()));
      response.setRefreshToken(jwtService.makeRefreshToken(request.getUserName()));
    }
    response.setSuccess(result);
    return response;
  }

  public FSQueryCaseResponseType queryDebuggingCase(String planId, String recordId) {
    if (StringUtils.isBlank(recordId)) {
      return new FSQueryCaseResponseType();
    }
    Pair<FSCaseDto, String> caseAndAppIdPair = storageCase.getViewRecord(recordId);
    FSCaseDto caseDto = caseAndAppIdPair.getLeft();
    String appId = caseAndAppIdPair.getRight();
    if (caseDto == null) {
      return new FSQueryCaseResponseType();
    }
    if (caseDto.getHeaders() == null) {
      caseDto.setHeaders(new ArrayList<>());
    }
    KeyValuePairDto header = new KeyValuePairDto();
    header.setKey(AREX_RECORD_ID);
    header.setValue(recordId);
    header.setActive(true);
    caseDto.getHeaders().add(0, header);
    KeyValuePairDto skipMockHeader = generateSkipMockHeader(appId);
    if(skipMockHeader != null) {
      caseDto.getHeaders().add(skipMockHeader);
    }

    if (StringUtils.equalsIgnoreCase(planId, NULL_PLAN_ID)) {
      String oldHost = caseDto.getHeaders().stream()
          .filter(h -> h.getKey().equalsIgnoreCase(HOST_KEY))
          .findFirst()
          .map(KeyValuePairDto::getValue)
          .orElse(null);
      String port;
      if (oldHost == null || !oldHost.contains(COLON)) {
        port = DEFAULT_PORT;
      } else {
        port = oldHost.split(COLON)[1];
      }
      caseDto.getAddress()
          .setEndpoint(contactUrl(LOCAL_HOST + COLON + port, caseDto.getAddress().getEndpoint()));
    } else {
      setAddressEndpoint(planId, caseDto.getAddress());
    }

    FSQueryCaseResponseType response = FSCaseMapper.INSTANCE.contractFromDto(caseDto);

    try {
      if (response.getBody() != null) {
        String body = response.getBody().getBody();
        response.getBody().setBody(ZstdUtils.base64Decode(body));
      }
    } catch (Exception e) {
      LogUtils.error(LOGGER, "Failed to decode body, recordId: {}", recordId);
    }

    return response;
  }

  public List<String> addItemsByAppAndInterface(FSAddItemsByAppAndInterfaceRequestType request,
      String userName) {
    List<String> path = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(request.getParentPath())) {
      path.addAll(request.getParentPath());
    } else {
      FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
      if (treeDto == null) {
        LogUtils.error(LOGGER, "Workspace not found, workspaceId: {}", request.getWorkspaceId());
        return null;
      }
      String[] defaultPath = new String[3];
      // add folder
      FSNodeDto appIdNode = fileSystemUtils.findByNodeName(treeDto.getRoots(),
          request.getAppName());
      if (appIdNode == null) {
        FSAddItemRequestType addFolderRequest = new FSAddItemRequestType();
        addFolderRequest.setId(treeDto.getId());
        addFolderRequest.setNodeName(request.getAppName());
        addFolderRequest.setNodeType(FSInfoItem.FOLDER);
        MutablePair<String, FSTreeDto> addFolder = addItem(addFolderRequest);
        if (addFolder == null) {
          LogUtils.error(LOGGER, "Add folder failed, workspaceId: {}, nodeName: {}",
              request.getWorkspaceId(),
              request.getAppName());
          return null;
        }
        appIdNode = fileSystemUtils.findByNodeName(addFolder.getRight().getRoots(),
            request.getAppName());
        defaultPath[0] = addFolder.getLeft();
      } else {
        defaultPath[0] = appIdNode.getInfoId();
      }
      // add interface
      FSNodeDto interfaceNode = fileSystemUtils.findByNodeName(appIdNode.getChildren(),
          request.getInterfaceName());
      if (interfaceNode == null) {
        FSAddItemRequestType addInterfaceRequest = new FSAddItemRequestType();
        addInterfaceRequest.setId(treeDto.getId());
        addInterfaceRequest.setNodeName(request.getInterfaceName());
        addInterfaceRequest.setNodeType(FSInfoItem.INTERFACE);
        addInterfaceRequest.setParentPath(Collections.singletonList(defaultPath[0]));
        MutablePair<String, FSTreeDto> addInterface = addItem(addInterfaceRequest);
        if (addInterface == null) {
          LogUtils.error(LOGGER, "Add interface failed, workspaceId: {}, nodeName: {}",
              request.getWorkspaceId(),
              request.getInterfaceName());
          return null;
        }
        interfaceNode = fileSystemUtils.findByNodeName(addInterface.getRight().getRoots(),
            request.getInterfaceName());
        defaultPath[1] = addInterface.getLeft();
      } else {
        defaultPath[1] = interfaceNode.getInfoId();
      }

      // add case
      FSNodeDto caseNode = fileSystemUtils.findByNodeName(interfaceNode.getChildren(),
          request.getNodeName());
      if (caseNode == null) {
        FSAddItemRequestType addCaseRequest = new FSAddItemRequestType();
        addCaseRequest.setId(treeDto.getId());
        addCaseRequest.setNodeName(request.getNodeName());
        addCaseRequest.setNodeType(FSInfoItem.CASE);
        addCaseRequest.setParentPath(Arrays.asList(defaultPath));
        MutablePair<String, FSTreeDto> addCase = addItem(addCaseRequest);
        if (addCase == null) {
          LogUtils.error(LOGGER, "Add case failed, workspaceId: {}, nodeName: {}",
              request.getWorkspaceId(),
              request.getNodeName());
          return null;
        }
        defaultPath[2] = addCase.getLeft();
      } else {
        defaultPath[2] = caseNode.getInfoId();
      }
      path.addAll(Arrays.asList(defaultPath));

      // save interface
      FSSaveInterfaceRequestType saveInterfaceRequest = buildSaveInterfaceRequest(request,
          defaultPath[1]);
      saveInterface(saveInterfaceRequest, userName);

      //save case
      FSSaveCaseRequestType saveCaseRequest = buildSaveCaseRequest(request, defaultPath[2]);
      saveCase(saveCaseRequest, userName);

      //pin case
      FSPinMockRequestType pinMockRequest = buildPinMockRequest(request, defaultPath[2]);
      pinMock(pinMockRequest);
    }


    // add the related information about the replay interface to the manual interface
    this.addReplayInfoToManual(request.getOperationId(), path);
    return path;
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
    interfaceDto.setHeaders(interfaceDto.getHeaders().stream()
        .filter(header -> !header.getKey().equalsIgnoreCase(AREX_RECORD_ID))
        .collect(Collectors.toList()));
    KeyValuePairDto kvDto = new KeyValuePairDto();
    kvDto.setKey(AREX_RECORD_ID);
    kvDto.setValue(newRecordId);
    kvDto.setActive(true);
    interfaceDto.getHeaders().add(0, kvDto);

    String configBatchNo = storageCase.getConfigBatchNo(newRecordId);
    if (StringUtils.isNotBlank(configBatchNo)) {
      interfaceDto.getHeaders()
          .add(new KeyValuePairDto(AREX_REPLAY_PREPARE_DEPENDENCY, PINNED_PRE_FIX + configBatchNo,
              true));
    }
    itemInfo.saveItem(itemDto);

    // update tree
    if (request.getNodeType() == FSInfoItem.CASE) {

      fsTreeRepository.updateFSTree(request.getWorkspaceId(), dto -> {
        if (dto == null) {
          return null;
        }
        FSNodeDto node = fileSystemUtils.deepFindByInfoId(dto.getRoots(), request.getInfoId());
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
    RecoveryService recoveryService = recoveryFactory.getRecoveryService(
        traceLogDto.getTraceType());
    return recoveryService.recovery(traceLogDto);
  }

  public MutablePair<Boolean, String> exportItem(FSExportItemRequestType request) {
    FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
    List<FSNodeDto> nodes;
    if (CollectionUtils.isEmpty(request.getPath())) {
      nodes = treeDto.getRoots();
    } else {
      List<String> path = request.getPath();
      FSNodeDto node = fileSystemUtils.findByPath(treeDto.getRoots(), path);
      nodes = Arrays.asList(node);
    }
    Map<Integer, List<String>> itemInfoIds = getItemInfoIds(nodes);
    Map<String, FSItemDto> itemInfos = getItemInfos(itemInfoIds);

    ImportExport ie = importExportFactory.getImportExport(request.getType());
    if (ie == null) {
      return new MutablePair<>(false, null);
    }
    String exportString = ie.exportItem(nodes, itemInfos);
    return new MutablePair<>(true, exportString);
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
    return ie.importItem(treeDto, request.getParentPath(), request.getImportString());
  }

  public KeyValuePairDto generateSkipMockHeader(String appId) {
    List<ScheduleConfiguration> scheduleConfigurations = scheduleConfigurationProvider.listBy(appId);
    if (CollectionUtils.isEmpty(scheduleConfigurations)) {
      return null;
    }
    ScheduleConfiguration scheduleConfiguration = scheduleConfigurations.get(0);
    if (MapUtils.isEmpty(scheduleConfiguration.getExcludeOperationMap())) {
      return null;
    }
    String kvValue = JsonUtils.toJsonString(scheduleConfiguration.getExcludeOperationMap());
    if (StringUtils.isBlank(kvValue)) {
      return null;
    }
    KeyValuePairDto kvDto = new KeyValuePairDto();
    kvDto.setKey(SKIP_MOCK_HEADER);
    kvDto.setValue(kvValue);
    kvDto.setActive(true);
    return kvDto;
  }

  private void setAddressEndpoint(String planId, AddressDto addressDto) {
    ReportPlanStatisticDto reportPlanStatisticDto = reportPlanStatisticRepository.findByPlanId(
        planId);
    if (addressDto != null) {
      addressDto.setEndpoint(this.contactUrl(
          reportPlanStatisticDto == null ? StringUtils.EMPTY
              : reportPlanStatisticDto.getTargetEnv(),
          addressDto.getEndpoint()));
    }
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

  private Map<Integer, Set<String>> removeItems(FSNodeDto fsNodeDto, String userName,
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

    fsTraceLogUtils.logDeleteItem(userName, workspaceId, fsNodeDto.getInfoId(), parentId, items,
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

  private void updateParentId(FSNodeDto fsNodeDto, String parentId, Integer parentNodeType) {
    if (fsNodeDto == null) {
      return;
    }
    ItemInfo itemInfo = itemInfoFactory.getItemInfo(fsNodeDto.getNodeType());
    FSItemDto fsItemDto = itemInfo.queryById(fsNodeDto.getInfoId());
    if (fsItemDto != null) {
      fsItemDto.setParentId(parentId);
      fsItemDto.setParentNodeType(parentNodeType);
      itemInfo.saveItem(fsItemDto);
    }
  }

  private Boolean sendInviteEmail(String arexUiUrl, String invitor, String invitee,
      String workspaceId,
      String token) {
    FSTreeDto workspace = fsTreeRepository.queryFSTreeById(workspaceId);
    if (workspace == null) {
      return false;
    }
    final String INVITATION_MAIL_SUBJECT = "[ArexTest]You are invited to '%s' workspace";

    InviteObject inviteObject = new InviteObject(invitee, workspaceId, token);
    String message;
    try {
      message = objectMapper.writeValueAsString(inviteObject);
    } catch (JsonProcessingException e) {
      LogUtils.error(LOGGER,
          String.format("sendInviteEmail writeValueAsString fail, invitor: %s", invitor));
      return false;
    }

    String address =
        arexUiUrl + "/click/?upn=" + Base64.getEncoder().encodeToString(message.getBytes());

    String context = loadResource.getResource(INVITATION_EMAIL_TEMPLATE);
    context = context.replace(SOMEBODY_PLACEHOLDER, invitor)
        .replace(WORKSPACE_NAME_PLACEHOLDER, workspace.getWorkspaceName())
        .replace(LINK_PLACEHOLDER, address);

    return mailService.sendEmail(invitee,
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

  private void addDuplicateItemFollowCurrent(List<FSNodeDto> treeDtos, FSNodeDto dupNodeDto,
      FSNodeDto currentDto) {
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

  private List<FSQueryItemType.ParentNodeType> getParentPath(String parentInfoId,
      Integer parentNodeType) {
    List<FSQueryItemType.ParentNodeType> parentPath = new ArrayList<>();
    while (StringUtils.isNotBlank(parentInfoId) && parentNodeType != null) {
      FSItemDto itemDto = itemInfoFactory.getItemInfo(parentNodeType).queryById(parentInfoId);
      if (itemDto == null) {
        break;
      }
      FSQueryItemType.ParentNodeType parentNode = new FSQueryItemType.ParentNodeType();
      parentNode.setId(itemDto.getId());
      parentNode.setNodeType(parentNodeType);
      parentNode.setName(itemDto.getName());
      parentPath.add(0, parentNode);
      parentInfoId = itemDto.getParentId();
      parentNodeType = itemDto.getParentNodeType();
    }
    return parentPath;
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

  public FSGetWorkspaceItemsResponseType getWorkspaceItems(FSGetWorkspaceItemsRequestType request) {
    FSGetWorkspaceItemsResponseType response = new FSGetWorkspaceItemsResponseType();

    FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
    if (treeDto == null) {
      return response;
    }
    FSNodeDto fsNodeDto = new FSNodeDto();
    fsNodeDto.setChildren(treeDto.getRoots());
    List<String> parentPath = request.getParentPath();
    if (CollectionUtils.isNotEmpty(parentPath)) {
      fsNodeDto = fileSystemUtils.findByPath(fsNodeDto.getChildren(), parentPath);
    }

    if (CollectionUtils.isNotEmpty(fsNodeDto.getChildren())) {
      fsNodeDto.getChildren().forEach(child -> {
        child.setExistChildren(CollectionUtils.isNotEmpty(child.getChildren()));
        child.setChildren(null);
      });
      fsNodeDto.setExistChildren(true);
    }
    FSNodeType fsNodeType = FSNodeMapper.INSTANCE.contractFromDto(fsNodeDto);
    response.setNode(fsNodeType);
    response.setPath(getAbsolutePath(fsNodeType.getInfoId(), fsNodeType.getNodeType()));
    return response;
  }

  public FSSearchWorkspaceItemsResponseType searchWorkspaceItems(
      FSSearchWorkspaceItemsRequestType request) {
    FSSearchWorkspaceItemsResponseType response = new FSSearchWorkspaceItemsResponseType();

    String keywords = request.getKeywords();
    String workspaceId = request.getWorkspaceId();
    int pageSize = request.getPageSize();
    List<LabelType> labelTypes = request.getLabels();

    List<String> includeLabelTypes = null;
    List<String> excludeLabelTypes = null;

    if (CollectionUtils.isNotEmpty(labelTypes)) {
      Map<String, List<LabelType>> groupLabelTypes = labelTypes.stream()
          .collect(Collectors.groupingBy(LabelType::getOperator));

      List<LabelType> eqLabelTypes = groupLabelTypes.get(EQUALS);
      if (CollectionUtils.isNotEmpty(eqLabelTypes)) {
        includeLabelTypes = eqLabelTypes.stream().map(LabelType::getValue)
            .collect(Collectors.toList());
      }
      List<LabelType> neLabelTypes = groupLabelTypes.get(NOT_EQUALS);
      if (CollectionUtils.isNotEmpty(neLabelTypes)) {
        excludeLabelTypes = neLabelTypes.stream().map(LabelType::getValue)
            .collect(Collectors.toList());
      }
    }

    queryAndBuildResponse(response, workspaceId, keywords, pageSize, includeLabelTypes,
        excludeLabelTypes);

    return response;
  }

  private void queryAndBuildResponse(FSSearchWorkspaceItemsResponseType response,
      String workspaceId, String keywords,
      int pageSize, List<String> includeLabelTypes, List<String> excludeLabelTypes) {

    CompletableFuture<Void> caseCompletableFuture = CompletableFuture.runAsync(() -> {
      List<FSItemDto> fsItemDtos = fsCaseRepository.queryCases(workspaceId, keywords,
          includeLabelTypes, excludeLabelTypes, pageSize);
      List<FSNodeType> fsNodeTypes = fsItemDtos.stream().map(
              fsItemDto -> {
                FSNodeType fsNodeType = FSNodeMapper.INSTANCE.contractFromFSItemDto(fsItemDto);
                fsNodeType.setNodeType(FSInfoItem.CASE);
                return fsNodeType;
              })
          .collect(Collectors.toList());
      response.setCaseNodes(fsNodeTypes);
    }, customForkJoinExecutor);
    CompletableFuture<Void> interfaceCompletableFuture = CompletableFuture.runAsync(() -> {
      List<FSInterfaceDto> fsInterfaceDtos = fsInterfaceRepository.queryInterfaces(workspaceId,
          keywords, includeLabelTypes, excludeLabelTypes, pageSize);
      List<FSNodeType> fsNodeTypes = fsInterfaceDtos.stream().map(
              fsInterfaceDto -> {
                FSNodeType fsNodeType = FSNodeMapper.INSTANCE.contractFromFSInterfaceDto(
                    fsInterfaceDto);
                fsNodeType.setNodeType(FSInfoItem.INTERFACE);
                return fsNodeType;
              })
          .collect(Collectors.toList());
      response.setInterfaceNodes(fsNodeTypes);
    }, customForkJoinExecutor);
    CompletableFuture<Void> folderCompletableFuture = CompletableFuture.runAsync(() -> {
      List<FSFolderDto> fsFolderDtos = fsFolderRepository.queryFolders(workspaceId, keywords,
          includeLabelTypes, excludeLabelTypes, pageSize);
      List<FSNodeType> fsNodeTypes = fsFolderDtos.stream().map(fsFolderDto -> {
            FSNodeType fsNodeType = FSNodeMapper.INSTANCE.contractFromFSItemDto(fsFolderDto);
            fsNodeType.setNodeType(FSInfoItem.FOLDER);
            return fsNodeType;
          })
          .collect(Collectors.toList());
      response.setFolderNodes(fsNodeTypes);
    }, customForkJoinExecutor);

    CompletableFuture.allOf(caseCompletableFuture, folderCompletableFuture,
        interfaceCompletableFuture).join();

  }


  public FSGetWorkspaceItemTreeResponseType getWorkspaceItemTree(
      FSGetWorkspaceItemTreeRequestType request) {

    FSGetWorkspaceItemTreeResponseType response = new FSGetWorkspaceItemTreeResponseType();

    FSTreeDto treeDto = fsTreeRepository.queryFSTreeById(request.getWorkspaceId());
    if (treeDto == null) {
      return response;
    }
    List<String> parentIds = this.getParentIds(request.getInfoId(), request.getNodeType());

    if (parentIds.isEmpty()) {
      return response;
    }

    FSNodeDto fsNodeDto;
    List<FSNodeDto> childrenFsNodeDtos = treeDto.getRoots();
    for (int i = parentIds.size() - 1; i >= 0; i--) {

      fsNodeDto = fileSystemUtils.findByInfoId(childrenFsNodeDtos, parentIds.get(i));
      if (fsNodeDto == null) {
        return response;
      }

      for (FSNodeDto fsNode : childrenFsNodeDtos) {
        fsNode.setExistChildren(CollectionUtils.isNotEmpty(fsNode.getChildren()));
        if (fsNode != fsNodeDto) {
          fsNode.setChildren(null);
        }
      }

      if (i == 0) {
        fsNodeDto.setChildren(null);
      }

      childrenFsNodeDtos = fsNodeDto.getChildren();
    }

    FSTreeType treeType = FSTreeMapper.INSTANCE.contractFromDto(treeDto);
    response.setFsTree(treeType);
    response.setPath(getAbsolutePath(request.getInfoId(), request.getNodeType()));
    return response;
  }

  private List<String> getParentIds(String infoId, int nodeType) {
    List<String> parentIds = new ArrayList<>();
    parentIds.add(infoId);

    switch (nodeType) {
      case FSInfoItem.CASE:
        FSItemDto fsItemDto = fsCaseRepository.queryCase(infoId, false);
        if (null == fsItemDto || StringUtils.isBlank(fsItemDto.getParentId())) {
          return parentIds;
        }
        infoId = fsItemDto.getParentId();
        parentIds.add(infoId);

        FSInterfaceDto fsInterfaceDto = fsInterfaceRepository.queryInterface(infoId);
        if (null == fsInterfaceDto || StringUtils.isBlank(fsInterfaceDto.getParentId())) {
          return parentIds;
        }
        infoId = fsInterfaceDto.getParentId();
        parentIds.add(infoId);
        break;
      case FSInfoItem.INTERFACE:
        fsInterfaceDto = fsInterfaceRepository.queryInterface(infoId);
        if (null == fsInterfaceDto || StringUtils.isBlank(fsInterfaceDto.getParentId())) {
          return parentIds;
        }
        infoId = fsInterfaceDto.getParentId();
        parentIds.add(infoId);
        break;
      case FSInfoItem.FOLDER:
        break;
      default:
        parentIds.clear();
        return parentIds;
    }
    // max parent level,avoid dead loops
    int level = 20;

    do {
      FSFolderDto fsFolderDto = fsFolderRepository.queryById(infoId);
      if (fsFolderDto == null || StringUtils.isBlank(fsFolderDto.getParentId())) {
        return parentIds;
      }
      infoId = fsFolderDto.getParentId();
      parentIds.add(infoId);
    } while (level-- > 0);

    return parentIds;
  }


  public BatchGetInterfaceCaseResponseType batchGetInterfaceCase(
      BatchGetInterfaceCaseRequestType request) {

    BatchGetInterfaceCaseResponseType response = new BatchGetInterfaceCaseResponseType();
    List<FSNodeType> fsNodeTypes = request.getNodes();
    if (CollectionUtils.isEmpty(fsNodeTypes)) {
      return response;
    }

    List<Object> nodes = Collections.synchronizedList(new ArrayList<>());
    List<CompletableFuture<Void>> completableFutures = Lists.newArrayList();
    fsNodeTypes.forEach(
        fsNodeType -> {
          if (FSInfoItem.CASE == fsNodeType.getNodeType()) {
            completableFutures
                .add(CompletableFuture.runAsync(
                    () -> this.getCaseNodes(nodes, fsNodeType.getInfoId()),
                    customForkJoinExecutor));
          } else if (FSInfoItem.INTERFACE == fsNodeType.getNodeType()) {
            completableFutures
                .add(CompletableFuture.runAsync(
                    () -> this.getInterfaceNodes(nodes, fsNodeType.getInfoId()),
                    customForkJoinExecutor));
          } else if (FSInfoItem.FOLDER == fsNodeType.getNodeType()) {
            completableFutures
                .add(CompletableFuture.runAsync(() -> {
                  List<String> ids = Lists.newArrayList();
                  this.queryLastChildFolder(
                      Collections.singletonList(fsNodeType.getInfoId()), ids);
                  List<FSInterfaceDto> fsInterfaceDtos = fsInterfaceRepository.queryInterfaceByParentIds(
                      ids);
                  if (CollectionUtils.isNotEmpty(fsInterfaceDtos)) {
                    fsInterfaceDtos.forEach(
                        fsInterfaceDto -> this.getInterfaceNodes(nodes,
                            fsInterfaceDto.getId()));
                  }
                }, customForkJoinExecutor));
          }
        });

    CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
    response.setNodes(nodes);
    return response;
  }

  private void getCaseNodes(List<Object> nodes, String infoId) {
    FSQueryCaseRequestType fsQueryCaseRequestType = new FSQueryCaseRequestType();
    fsQueryCaseRequestType.setId(infoId);
    fsQueryCaseRequestType.setGetCompareMsg(false);
    FSQueryCaseResponseType fsQueryCaseResponseType = this.queryCase(fsQueryCaseRequestType);
    nodes.add(fsQueryCaseResponseType);
  }

  private void getInterfaceNodes(List<Object> nodes, String infoId) {
    FSQueryInterfaceRequestType fsQueryInterfaceRequestType = new FSQueryInterfaceRequestType();
    fsQueryInterfaceRequestType.setId(infoId);
    FSQueryInterfaceResponseType fsQueryInterfaceResponseType = this.queryInterface(
        fsQueryInterfaceRequestType);
    nodes.add(fsQueryInterfaceResponseType);

    List<FSItemDto> fsItemDtos = fsCaseRepository.queryCasesByParentIds(
        Collections.singletonList(infoId));

    if (CollectionUtils.isNotEmpty(fsItemDtos)) {
      fsItemDtos.forEach(fsItemDto -> this.getCaseNodes(nodes, fsItemDto.getId()));
    }
  }

  private void queryLastChildFolder(List<String> ids, List<String> result) {
    List<FSItemDto> children = fsFolderRepository.queryByIdsByParentIds(ids);
    if (CollectionUtils.isNotEmpty(children)) {
      Set<String> pids = children.stream().map(FSItemDto::getParentId).collect(Collectors.toSet());
      ids.stream().filter(id -> !pids.contains(id)).forEach(result::add);
    } else {
      result.addAll(ids);
      return;
    }
    ids = children.stream().map(FSItemDto::getId).collect(Collectors.toList());
    queryLastChildFolder(ids, result);
  }

  private List<String> getAbsolutePath(String infoId, Integer nodeType) {
    return getAbsolutePathInfo(infoId, nodeType)
        .stream()
        .map(FSPathInfoDto::getId)
        .collect(Collectors.toList());
  }

  public List<FSPathInfoDto> getAbsolutePathInfo(String infoId, Integer nodeType) {
    List<FSPathInfoDto> path = Lists.newArrayList();
    if (StringUtils.isBlank(infoId)) {
      return path;
    }
    String curInfoId = infoId;
    Integer curNodeType = nodeType;
    while (true) {
      ItemInfo itemInfo = itemInfoFactory.getItemInfo(curNodeType);
      if (itemInfo == null) {
        throw new ArexException(ArexApiResponseCode.FS_UNKNOWN_NODE_TYPE,
            "Unknown node type: " + nodeType);
      }
      FSItemDto fsItemDto = itemInfo.queryById(curInfoId);
      if (fsItemDto == null) {
        break;
      }
      path.add(0, new FSPathInfoDto(fsItemDto.getId(), fsItemDto.getName()));
      curInfoId = fsItemDto.getParentId();
      curNodeType = fsItemDto.getParentNodeType();
      if (StringUtils.isEmpty(curInfoId)) {
        break;
      }
    }
    return path;
  }

  private FSSaveInterfaceRequestType buildSaveInterfaceRequest(
      FSAddItemsByAppAndInterfaceRequestType in, String id) {
    FSSaveInterfaceRequestType out = new FSSaveInterfaceRequestType();
    out.setAddress(in.getAddress());
    out.setBody(in.getBody());
    out.setId(id);
    out.setHeaders(in.getHeaders());
    out.setLabelIds(in.getLabelIds());
    out.setAuth(in.getAuth());
    out.setDescription(in.getDescription());
    out.setParams(in.getParams());
    out.setWorkspaceId(in.getWorkspaceId());
    out.setPreRequestScripts(in.getPreRequestScripts());
    out.setTestAddress(in.getTestAddress());
    out.setTestScripts(in.getTestScripts());
    return out;
  }

  private FSSaveCaseRequestType buildSaveCaseRequest(FSAddItemsByAppAndInterfaceRequestType in,
      String id) {
    FSSaveCaseRequestType out = new FSSaveCaseRequestType();
    out.setId(id);
    out.setAddress(in.getAddress());
    out.setBody(in.getBody());
    out.setHeaders(in.getHeaders());
    out.setLabelIds(in.getLabelIds());
    out.setAuth(in.getAuth());
    out.setDescription(in.getDescription());
    out.setParams(in.getParams());
    out.setWorkspaceId(in.getWorkspaceId());
    out.setPreRequestScripts(in.getPreRequestScripts());
    out.setTestAddress(in.getTestAddress());
    out.setTestScripts(in.getTestScripts());
    return out;
  }

  private FSPinMockRequestType buildPinMockRequest(FSAddItemsByAppAndInterfaceRequestType in,
      String id) {
    FSPinMockRequestType out = new FSPinMockRequestType();
    out.setNodeType(FSInfoItem.CASE);
    out.setRecordId(in.getNodeName());
    out.setWorkspaceId(in.getWorkspaceId());
    out.setInfoId(id);
    return out;
  }
}
