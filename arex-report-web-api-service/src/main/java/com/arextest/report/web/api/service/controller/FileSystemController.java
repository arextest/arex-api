package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.filesystem.FileSystemService;
import com.arextest.report.core.business.filesystem.RolePermission;
import com.arextest.report.model.api.contracts.SuccessResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSAddWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddWorkspaceResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSDeleteWorkspaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSDuplicateRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSMoveItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryCaseRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryCaseResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryInterfaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSQueryInterfaceResponseType;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Response addItem(@RequestBody FSAddItemRequestType request) {
        if (StringUtils.isNotEmpty(request.getId()) &&
                !rolePermission.checkPermission(RolePermission.ADD_ITEM,
                        request.getUserName(),
                        request.getId())) {
            return ResponseUtils.errorResponse("no permission", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
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
    public Response removeItem(@RequestBody FSRemoveItemRequestType request) {
        if (!rolePermission.checkPermission(RolePermission.REMOVE_ITEM, request.getUserName(), request.getId())) {
            return ResponseUtils.errorResponse("no permission", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
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
    public Response rename(@RequestBody FSRenameRequestType request) {
        if (!rolePermission.checkPermission(RolePermission.RENAME_ITEM, request.getUserName(), request.getId())) {
            return ResponseUtils.errorResponse("no permission", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
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
    public Response duplicate(@RequestBody FSDuplicateRequestType request) {
        if (!rolePermission.checkPermission(RolePermission.DUPLICATE_ITEM, request.getUserName(), request.getId())) {
            return ResponseUtils.errorResponse("no permission", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
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
    public Response deleteWorkspace(@RequestBody FSDeleteWorkspaceRequestType request) {
        if (!rolePermission.checkPermission(RolePermission.DELETE_WORKSPACE_ACTION,
                request.getUserName(),
                request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("no permission", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
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
    public Response renameWorkspace(@RequestBody FSRenameWorkspaceRequestType request) {
        if (!rolePermission.checkPermission(RolePermission.RENAME_WORKSPACE, request.getUserName(), request.getId())) {
            return ResponseUtils.errorResponse("no permission", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
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
    public Response queryWorkspaceById(@RequestBody FSQueryWorkspaceRequestType request) {
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
    public Response inviteToWorkspace(@RequestBody InviteToWorkspaceRequestType request) {
        if (!rolePermission.checkPermission(RolePermission.INVITE_TO_WORKSPACE,
                request.getInvitor(),
                request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("no permission", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
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
    public Response leaveWorkspace(@RequestBody LeaveWorkspaceRequestType request) {
        if (StringUtils.isEmpty(request.getUserName())) {
            return ResponseUtils.errorResponse("UserName cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
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
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(fileSystemService.validInvitation(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }
}
