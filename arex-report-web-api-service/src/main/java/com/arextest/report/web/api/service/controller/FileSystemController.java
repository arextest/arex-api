package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.filesystem.FileSystemService;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSAddItemResponseType;
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
import com.arextest.report.model.api.contracts.filesystem.FSSaveCaseRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveCaseResponseType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveInterfaceRequestType;
import com.arextest.report.model.api.contracts.filesystem.FSSaveInterfaceResponseType;
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

    @PostMapping("/addItem")
    @ResponseBody
    public Response addItem(@RequestBody FSAddItemRequestType request) {
        if (StringUtils.isEmpty(request.getNodeName())) {
            return ResponseUtils.errorResponse("Node name cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
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
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("id cannot be empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getRemoveNodePath())) {
            return ResponseUtils.errorResponse("remove node path cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            FSRemoveItemResponseType response = fileSystemService.removeItem(request);
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
}
