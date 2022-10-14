package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.common.JwtUtil;
import com.arextest.report.common.Tuple;
import com.arextest.report.core.business.filesystem.FileSystemService;
import com.arextest.report.core.business.filesystem.RolePermission;
import com.arextest.report.model.api.contracts.SuccessResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemFromRecordRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemFromRecordResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSAddWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddWorkspaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSDeleteWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSDuplicateRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSExportItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSExportItemResponseType;
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
import com.arextest.report.model.api.contracts.filesystem.FSRemoveItemResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSRenameRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSRenameResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSRenameWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveCaseResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveInterfaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveInterfaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.InviteToWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.InviteToWorkspaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.LeaveWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.ValidInvitationRequestType;
import com.arextest.report.model.api.contracts.filesystem.ValidInvitationResponseType;
import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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
            @RequestBody FSAddItemRequestType request) {
        if (StringUtils.isNotEmpty(request.getId()) &&
                !rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM,
                        token,
                        request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        if (StringUtils.isEmpty(request.getNodeName())) {
            return ResponseUtils.errorResponse("Node name cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (request.getNodeType() == null) {
            return ResponseUtils.errorResponse("NodeType cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            FSAddItemResponseType response = fileSystemService.addItem(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/removeItem")
    @ResponseBody
    public Response removeItem(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody FSRemoveItemRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("id cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (request.getRemoveNodePath() == null || request.getRemoveNodePath().length == 0) {
            return ResponseUtils.errorResponse("remove node path cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            FSRemoveItemResponseType response = new FSRemoveItemResponseType();
            response.setSuccess(fileSystemService.removeItem(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/rename")
    @ResponseBody
    public Response rename(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody FSRenameRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("Cannot rename item because workspace id is empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (request.getPath() == null || request.getPath().length == 0) {
            return ResponseUtils.errorResponse("Old name cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        FSRenameResponseType response = new FSRenameResponseType();
        try {
            Boolean success = fileSystemService.rename(request);
            response.setSuccess(success);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/duplicate")
    @ResponseBody
    public Response duplicate(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody FSDuplicateRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("Cannot duplicate item because workspace id is empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (request.getPath() == null || request.getPath().length == 0) {
            return ResponseUtils.errorResponse("Item path cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(fileSystemService.duplicate(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/move")
    @ResponseBody
    public Response move(@RequestBody FSMoveItemRequestType request) {
        if (request.getFromNodePath() == null || request.getFromNodePath().length == 0) {
            return ResponseUtils.errorResponse("source item cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(fileSystemService.move(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/addWorkspace")
    @ResponseBody
    public Response addWorkspace(@RequestBody FSAddWorkspaceRequestType request) {
        if (StringUtils.isEmpty(request.getUserName())) {
            return ResponseUtils.errorResponse("userName cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            FSAddWorkspaceResponseType response = fileSystemService.addWorkspace(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/deleteWorkspace")
    @ResponseBody
    public Response deleteWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody FSDeleteWorkspaceRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE,
                token,
                request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("workspace id cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(fileSystemService.deleteWorkspace(request.getWorkspaceId()));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/renameWorkspace")
    @ResponseBody
    public Response renameWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody FSRenameWorkspaceRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("workspaceId cannot empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getWorkspaceName())) {
            return ResponseUtils.errorResponse("new workspace name cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(fileSystemService.renameWorkspace(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/queryWorkspaceById")
    @ResponseBody
    public Response queryWorkspaceById(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody FSQueryWorkspaceRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.VIEW_WORKSPACE, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        try {
            FSQueryWorkspaceResponseType response = fileSystemService.queryWorkspaceById(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/queryWorkspacesByUser")
    @ResponseBody
    public Response queryWorkspaces(@RequestBody FSQueryWorkspacesRequestType request) {
        try {
            FSQueryWorkspacesResponseType response = fileSystemService.queryWorkspacesByUser(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/queryUsersByWorkspace")
    @ResponseBody
    public Response queryUsersByWorkspace(@RequestBody FSQueryUsersByWorkspaceRequestType request) {
        try {
            FSQueryUsersByWorkspaceResponseType response = fileSystemService.queryUsersByWorkspace(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/saveInterface")
    @ResponseBody
    public Response saveInterface(@RequestBody FSSaveInterfaceRequestType request) {
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("Interface id cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            FSSaveInterfaceResponseType response = fileSystemService.saveInterface(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/queryInterface")
    @ResponseBody
    public Response queryInterface(@RequestBody FSQueryInterfaceRequestType request) {
        try {
            FSQueryInterfaceResponseType response = fileSystemService.queryInterface(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/saveCase")
    @ResponseBody
    public Response saveCase(@RequestBody FSSaveCaseRequestType request) {
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("Case id cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            FSSaveCaseResponseType response = fileSystemService.saveCase(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/queryCase")
    @ResponseBody
    public Response queryCase(@RequestBody FSQueryCaseRequestType request) {
        try {
            FSQueryCaseResponseType response = fileSystemService.queryCase(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/inviteToWorkspace")
    @ResponseBody
    public Response inviteToWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody InviteToWorkspaceRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.INVITE_TO_WORKSPACE,
                token,
                request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        if (request.getUserNames() == null || request.getUserNames().size() == 0) {
            return ResponseUtils.errorResponse("UserNames cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("Workspace Id cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            InviteToWorkspaceResponseType response = fileSystemService.inviteToWorkspace(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/leaveWorkspace")
    @ResponseBody
    public Response leaveWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody LeaveWorkspaceRequestType request) {
        String userName = JwtUtil.getUserName(token);
        if (StringUtils.isEmpty(userName)) {
            return ResponseUtils.errorResponse("Incorrect token. Please login again.",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("WorkspaceId cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(fileSystemService.leaveWorkspace(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/validInvitation")
    @ResponseBody
    public Response validInvitation(@RequestBody ValidInvitationRequestType request) {
        if (StringUtils.isEmpty(request.getUserName())) {
            return ResponseUtils.errorResponse("UserName cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("WorkspaceId cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getToken())) {
            return ResponseUtils.errorResponse("Token cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            ValidInvitationResponseType responseType = fileSystemService.validInvitation(request);
            return ResponseUtils.successResponse(responseType);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/addItemFromRecord")
    @ResponseBody
    public Response addItemFromRecord(@RequestBody FSAddItemFromRecordRequestType request) {
        if (StringUtils.isEmpty(request.getRecordId())) {
            return ResponseUtils.errorResponse("RecordId cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("WorkspaceId cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            Tuple<String, String> result = fileSystemService.addItemFromRecord(request);
            if (result == null) {
                return ResponseUtils.errorResponse("Failed to add record case to workspace",
                        ResponseCode.REQUESTED_HANDLE_EXCEPTION);
            }
            FSAddItemFromRecordResponseType response = new FSAddItemFromRecordResponseType();
            response.setSuccess(true);
            response.setWorkspaceId(result.x);
            response.setInfoId(result.y);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/export")
    @ResponseBody
    public Response exportItem(@RequestBody FSExportItemRequestType request) {
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("WorkspaceId cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            FSExportItemResponseType response = new FSExportItemResponseType();
            Tuple<Boolean, String> result = fileSystemService.exportItem(request);
            if (!result.x) {
                return ResponseUtils.errorResponse("Failed to export items", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
            }
            response.setExportString(result.y);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/import")
    @ResponseBody
    public Response importItem(@RequestBody FSImportItemRequestType request) {
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("WorkspaceId cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getImportString())) {
            return ResponseUtils.errorResponse("Import string cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(fileSystemService.importItem(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }
}
