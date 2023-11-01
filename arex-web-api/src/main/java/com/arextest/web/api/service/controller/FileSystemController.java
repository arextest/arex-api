package com.arextest.web.api.service.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

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

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.JwtUtil;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.filesystem.FileSystemService;
import com.arextest.web.core.business.filesystem.RolePermission;
import com.arextest.web.model.contract.contracts.SuccessResponseType;
import com.arextest.web.model.contract.contracts.filesystem.ChangeRoleRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemFromRecordRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemFromRecordResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddItemResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSAddWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.FSDeleteWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSDuplicateRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSExportItemRequestType;
import com.arextest.web.model.contract.contracts.filesystem.FSExportItemResponseType;
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
import com.arextest.web.model.contract.contracts.filesystem.FsAddItemFromRecordByDefaultRequestType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.InviteToWorkspaceResponseType;
import com.arextest.web.model.contract.contracts.filesystem.LeaveWorkspaceRequestType;
import com.arextest.web.model.contract.contracts.filesystem.RecoverItemInfoRequestType;
import com.arextest.web.model.contract.contracts.filesystem.RemoveUserFromWorkspaceType;
import com.arextest.web.model.contract.contracts.filesystem.ValidInvitationRequestType;
import com.arextest.web.model.contract.contracts.filesystem.ValidInvitationResponseType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/filesystem/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileSystemController {

    @Resource
    private FileSystemService fileSystemService;

    @Resource
    private RolePermission rolePermission;

    @PostMapping("/addItem")
    @ResponseBody
    public Response addItem(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @Valid @RequestBody FSAddItemRequestType request) {
        if (StringUtils.isNotEmpty(request.getId())
            && !rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        String userName = JwtUtil.getUserName(token);
        request.setUserName(userName);
        FSAddItemResponseType response = fileSystemService.addItemForController(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/removeItem")
    @ResponseBody
    public Response removeItem(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @Valid @RequestBody FSRemoveItemRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        String userName = JwtUtil.getUserName(token);
        FSRemoveItemResponseType response = new FSRemoveItemResponseType();
        response.setSuccess(fileSystemService.removeItem(request, userName));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/rename")
    @ResponseBody
    public Response rename(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @Valid @RequestBody FSRenameRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        FSRenameResponseType response = new FSRenameResponseType();
        Boolean success = fileSystemService.rename(request);
        response.setSuccess(success);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/duplicate")
    @ResponseBody
    public Response duplicate(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @Valid @RequestBody FSDuplicateRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(fileSystemService.duplicate(request));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/move")
    @ResponseBody
    public Response move(@Valid @RequestBody FSMoveItemRequestType request) {
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(fileSystemService.move(request));
        return ResponseUtils.successResponse(response);
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
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token, request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(fileSystemService.deleteWorkspace(request.getWorkspaceId()));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/renameWorkspace")
    @ResponseBody
    public Response renameWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @Valid @RequestBody FSRenameWorkspaceRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(fileSystemService.renameWorkspace(request));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/queryWorkspaceById")
    @ResponseBody
    public Response queryWorkspaceById(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @RequestBody FSQueryWorkspaceRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.VIEW_WORKSPACE, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
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
        String userName = JwtUtil.getUserName(token);
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
        String userName = JwtUtil.getUserName(token);
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
        String userName = JwtUtil.getUserName(token);
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
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
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
    public Response removeUserFromWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @Valid @RequestBody RemoveUserFromWorkspaceType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token, request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(fileSystemService.leaveWorkspace(request.getUserName(), request.getWorkspaceId()));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/changeRole")
    @ResponseBody
    public Response changeRole(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @Valid @RequestBody ChangeRoleRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token, request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(fileSystemService.changeRole(request));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/leaveWorkspace")
    @ResponseBody
    public Response leaveWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
        @Valid @RequestBody LeaveWorkspaceRequestType request) {
        String userName = JwtUtil.getUserName(token);
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

    @PostMapping("/addItemFromRecord")
    @ResponseBody
    public Response addItemFromRecord(@Valid @RequestBody FSAddItemFromRecordRequestType request) {
        MutablePair<String, String> result = fileSystemService.addItemFromRecord(request);
        if (result == null) {
            return ResponseUtils.errorResponse("Failed to add record case to workspace",
                ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        FSAddItemFromRecordResponseType response = new FSAddItemFromRecordResponseType();
        response.setSuccess(true);
        response.setWorkspaceId(result.getLeft());
        response.setInfoId(result.getRight());
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/addItemFromRecordByDefault")
    @ResponseBody
    public Response addItemFromRecordByDefault(@Valid @RequestBody FsAddItemFromRecordByDefaultRequestType request) {
        MutablePair<String, String> result = fileSystemService.addItemFromRecordByDefault(request);
        if (result == null) {
            return ResponseUtils.errorResponse("Failed to add record case to workspace by default path",
                ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        FSAddItemFromRecordResponseType response = new FSAddItemFromRecordResponseType();
        response.setSuccess(true);
        response.setWorkspaceId(result.getLeft());
        response.setInfoId(result.getRight());
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
            return ResponseUtils.errorResponse("Failed to export items", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
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
}
