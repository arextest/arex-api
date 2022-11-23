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
import javax.validation.Valid;

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
        if (StringUtils.isNotEmpty(request.getId()) &&
                !rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM,
                        token,
                        request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        FSAddItemResponseType response = fileSystemService.addItem(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/removeItem")
    @ResponseBody
    public Response removeItem(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @Valid @RequestBody FSRemoveItemRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ITEM, token, request.getId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        FSRemoveItemResponseType response = new FSRemoveItemResponseType();
        response.setSuccess(fileSystemService.removeItem(request));
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
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_WORKSPACE,
                token,
                request.getWorkspaceId())) {
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

    @PostMapping("/saveInterface")
    @ResponseBody
    public Response saveInterface(@RequestBody FSSaveInterfaceRequestType request) {
        FSSaveInterfaceResponseType response = fileSystemService.saveInterface(request);
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
    public Response saveCase(@RequestBody FSSaveCaseRequestType request) {
        FSSaveCaseResponseType response = fileSystemService.saveCase(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/queryCase")
    @ResponseBody
    public Response queryCase(@RequestBody FSQueryCaseRequestType request) {
        FSQueryCaseResponseType response = fileSystemService.queryCase(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/inviteToWorkspace")
    @ResponseBody
    public Response inviteToWorkspace(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @Valid @RequestBody InviteToWorkspaceRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.INVITE_TO_WORKSPACE,
                token,
                request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        InviteToWorkspaceResponseType response = fileSystemService.inviteToWorkspace(request);
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
        response.setSuccess(fileSystemService.leaveWorkspace(request));
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
    }

    @PostMapping("/export")
    @ResponseBody
    public Response exportItem(@Valid @RequestBody FSExportItemRequestType request) {
        FSExportItemResponseType response = new FSExportItemResponseType();
        Tuple<Boolean, String> result = fileSystemService.exportItem(request);
        if (!result.x) {
            return ResponseUtils.errorResponse("Failed to export items", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        response.setExportString(result.y);
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
