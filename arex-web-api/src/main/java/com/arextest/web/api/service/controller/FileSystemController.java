package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.jwt.JWTService;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.filesystem.FileSystemService;
import com.arextest.web.core.business.filesystem.RolePermission;
import com.arextest.web.model.contract.contracts.SuccessResponseType;
import com.arextest.web.model.contract.contracts.filesystem.BatchGetInterfaceCaseRequestType;
import com.arextest.web.model.contract.contracts.filesystem.BatchGetInterfaceCaseResponseType;
import com.arextest.web.model.contract.contracts.filesystem.ChangeRoleRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemsByAppAndInterfaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemsByAppAndInterfaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSDeleteWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSDuplicateRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSDuplicateResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSExportItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSExportItemResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetPathInfoRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetPathInfoResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetWorkspaceItemTreeRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetWorkspaceItemTreeResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetWorkspaceItemsRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSGetWorkspaceItemsResponseType;
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
import com.arextest.web.model.contract.contracts.filesystem.FSRemoveItemResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSRenameRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSRenameResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSRenameWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveCaseResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveFolderRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveFolderResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveInterfaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSaveInterfaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSSearchWorkspaceItemsRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSSearchWorkspaceItemsResponseType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.LeaveWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.RecoverItemInfoRequestType;
import com.arextest.web.model.contract.contracts.filesystem.RemoveUserFromWorkspaceType;
import com.arextest.web.model.contract.contracts.filesystem.ValidInvitationRequestType;
import com.arextest.web.model.contract.contracts.filesystem.ValidInvitationResponseType;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/api/filesystem/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileSystemController {

  @Resource
  private FileSystemService fileSystemService;

  @Resource
  private RolePermission rolePermission;


  @Resource
  private JWTService jwtService;

  @PostMapping("/addItem")
  @ResponseBody
  public Response addItem(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody FSAddItemRequestType request) {
    if (StringUtils.isNotEmpty(request.getId())
        && !rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token,
        request.getId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    String userName = jwtService.getUserName(token);
    request.setUserName(userName);
    FSAddItemResponseType response = fileSystemService.addItemForController(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/removeItem")
  @ResponseBody
  public Response removeItem(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody FSRemoveItemRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    String userName = jwtService.getUserName(token);
    FSRemoveItemResponseType response = new FSRemoveItemResponseType();
    MutablePair<Boolean, List<String>> result = fileSystemService.removeItem(request, userName);
    response.setSuccess(result.getLeft());
    response.setPath(result.getRight());
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/rename")
  @ResponseBody
  public Response rename(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody FSRenameRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    FSRenameResponseType response = new FSRenameResponseType();
    MutablePair<Boolean, List<String>> result = fileSystemService.rename(request);
    response.setSuccess(result.getLeft());
    response.setPath(result.getRight());
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/duplicate")
  @ResponseBody
  public Response duplicate(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody FSDuplicateRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    FSDuplicateResponseType response = fileSystemService.duplicate(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/move")
  @ResponseBody
  public Response move(@Valid @RequestBody FSMoveItemRequestType request) {
    return ResponseUtils.successResponse(fileSystemService.move(request));
  }

  @PostMapping("/addWorkspace")
  @ResponseBody
  public Response addWorkspace(@Valid @RequestBody FSAddWorkspaceRequestType request) {
    FSAddWorkspaceResponseType response = fileSystemService.addWorkspace(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/deleteWorkspace")
  @ResponseBody
  public Response deleteWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody FSDeleteWorkspaceRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token,
        request.getWorkspaceId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(fileSystemService.deleteWorkspace(request.getWorkspaceId()));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/renameWorkspace")
  @ResponseBody
  public Response renameWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody FSRenameWorkspaceRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token,
        request.getId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(fileSystemService.renameWorkspace(request));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryWorkspaceById")
  @ResponseBody
  public Response queryWorkspaceById(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @RequestBody FSQueryWorkspaceRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.VIEW_WORKSPACE, token,
        request.getId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    FSQueryWorkspaceResponseType response = fileSystemService.queryWorkspaceById(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryWorkspacesByUser")
  @ResponseBody
  public Response queryWorkspaces(@RequestBody FSQueryWorkspacesRequestType request) {
    FSQueryWorkspacesResponseType response = fileSystemService.queryWorkspacesByUser(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryUsersByWorkspace")
  @ResponseBody
  public Response queryUsersByWorkspace(@RequestBody FSQueryUsersByWorkspaceRequestType request) {
    FSQueryUsersByWorkspaceResponseType response = fileSystemService.queryUsersByWorkspace(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/saveFolder")
  @ResponseBody
  public Response saveFolder(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @RequestBody FSSaveFolderRequestType request) {
    String userName = jwtService.getUserName(token);
    FSSaveFolderResponseType response = fileSystemService.saveFolder(request, userName);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryFolder")
  @ResponseBody
  public Response queryFolder(@RequestBody FSQueryFolderRequestType request) {
    FSQueryFolderResponseType response = fileSystemService.queryFolder(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/saveInterface")
  @ResponseBody
  public Response saveInterface(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @RequestBody FSSaveInterfaceRequestType request) {
    String userName = jwtService.getUserName(token);
    FSSaveInterfaceResponseType response = new FSSaveInterfaceResponseType();
    response.setSuccess(fileSystemService.saveInterface(request, userName));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryInterface")
  @ResponseBody
  public Response queryInterface(@RequestBody FSQueryInterfaceRequestType request) {
    FSQueryInterfaceResponseType response = fileSystemService.queryInterface(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/saveCase")
  @ResponseBody
  public Response saveCase(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @RequestBody FSSaveCaseRequestType request) {
    String userName = jwtService.getUserName(token);
    FSSaveCaseResponseType response = new FSSaveCaseResponseType();
    response.setSuccess(fileSystemService.saveCase(request, userName));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryCase")
  @ResponseBody
  public Response queryCase(@RequestBody FSQueryCaseRequestType request) {
    FSQueryCaseResponseType response = fileSystemService.queryCase(request);
    return ResponseUtils.successResponse(response);
  }

  @GetMapping("/queryDebuggingCase/{planId}/{recordId}")
  @ResponseBody
  public Response queryDebuggingCase(@PathVariable String planId, @PathVariable String recordId) {
    FSQueryCaseResponseType response = fileSystemService.queryDebuggingCase(planId, recordId);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/inviteToWorkspace")
  @ResponseBody
  public Response inviteToWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody InviteToWorkspaceRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.INVITE_TO_WORKSPACE, token,
        request.getWorkspaceId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    InviteToWorkspaceResponseType response = fileSystemService.inviteToWorkspace(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/recoverItemInfo")
  @ResponseBody
  public Response recoverItemInfo(@Valid @RequestBody RecoverItemInfoRequestType request) {
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(fileSystemService.recovery(request));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/removeUserFromWorkspace")
  @ResponseBody
  public Response removeUserFromWorkspace(
      @RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody RemoveUserFromWorkspaceType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token,
        request.getWorkspaceId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(
        fileSystemService.leaveWorkspace(request.getUserName(), request.getWorkspaceId()));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/changeRole")
  @ResponseBody
  public Response changeRole(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody ChangeRoleRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token,
        request.getWorkspaceId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(fileSystemService.changeRole(request));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/leaveWorkspace")
  @ResponseBody
  public Response leaveWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody LeaveWorkspaceRequestType request) {
    String userName = jwtService.getUserName(token);
    if (StringUtils.isEmpty(userName)) {
      return ResponseUtils.errorResponse("Incorrect token. Please login again.",
          ResponseCode.REQUESTED_PARAMETER_INVALID);
    }
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(fileSystemService.leaveWorkspace(userName, request.getWorkspaceId()));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/validInvitation")
  @ResponseBody
  public Response validInvitation(@Valid @RequestBody ValidInvitationRequestType request) {
    ValidInvitationResponseType responseType = fileSystemService.validInvitation(request);
    return ResponseUtils.successResponse(responseType);
  }

  @PostMapping("/addItemsByAppNameAndInterfaceName")
  @ResponseBody
  public Response addItemsByAppAndInterface(
      @Valid @RequestBody FSAddItemsByAppAndInterfaceRequestType request) {
    List<String> path = fileSystemService.addItemsByAppAndInterface(request);
    FSAddItemsByAppAndInterfaceResponseType response = new FSAddItemsByAppAndInterfaceResponseType();
    if (CollectionUtils.isEmpty(path)) {
      response.setSuccess(false);
    } else {
      response.setSuccess(true);
      response.setPath(path);
      response.setWorkspaceId(request.getWorkspaceId());
    }
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/pinMock")
  @ResponseBody
  public Response pinMock(@Valid @RequestBody FSPinMockRequestType request) {
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(fileSystemService.pinMock(request));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/export")
  @ResponseBody
  public Response exportItem(@Valid @RequestBody FSExportItemRequestType request) {
    FSExportItemResponseType response = new FSExportItemResponseType();
    MutablePair<Boolean, String> result = fileSystemService.exportItem(request);
    if (!result.getLeft()) {
      return ResponseUtils.errorResponse("Failed to export items",
          ResponseCode.REQUESTED_HANDLE_EXCEPTION);
    }
    response.setExportString(result.getRight());
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/import")
  @ResponseBody
  public Response importItem(@Valid @RequestBody FSImportItemRequestType request) {
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(fileSystemService.importItem(request));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/getWorkspaceItem")
  @ResponseBody
  public Response getWorkspaceItem(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @RequestBody FSGetWorkspaceItemsRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.VIEW_WORKSPACE, token,
        request.getWorkspaceId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    FSGetWorkspaceItemsResponseType response = fileSystemService.getWorkspaceItems(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/searchWorkspaceItems")
  @ResponseBody
  public Response searchWorkspaceItems(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @RequestBody FSSearchWorkspaceItemsRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.VIEW_WORKSPACE, token,
        request.getWorkspaceId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    FSSearchWorkspaceItemsResponseType response = fileSystemService.searchWorkspaceItems(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/getWorkspaceItemTree")
  @ResponseBody
  public Response getWorkspaceItemTree(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @RequestBody FSGetWorkspaceItemTreeRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.VIEW_WORKSPACE, token,
        request.getWorkspaceId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    FSGetWorkspaceItemTreeResponseType response = fileSystemService.getWorkspaceItemTree(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/batchGetInterfaceCase")
  @ResponseBody
  public Response batchGetInterfaceCase(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @RequestBody BatchGetInterfaceCaseRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.VIEW_WORKSPACE, token,
        request.getWorkspaceId())) {
      return ResponseUtils.errorResponse(Constants.NO_PERMISSION,
          ResponseCode.AUTHENTICATION_FAILED);
    }
    BatchGetInterfaceCaseResponseType response = fileSystemService.batchGetInterfaceCase(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/getPathInfo")
  @ResponseBody
  public Response getPathInfo(@RequestBody FSGetPathInfoRequestType request) {
    FSGetPathInfoResponseType response = new FSGetPathInfoResponseType();
    response.setPathInfo(
        fileSystemService.getAbsolutePathInfo(request.getInfoId(), request.getNodeType()));
    return ResponseUtils.successResponse(response);
  }

}
