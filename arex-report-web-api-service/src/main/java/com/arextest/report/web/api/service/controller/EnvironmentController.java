package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.EnvironmentService;
import com.arextest.report.core.business.filesystem.RolePermission;
import com.arextest.report.model.api.contracts.SuccessResponseType;
import com.arextest.report.model.api.contracts.environment.DuplicateEnvironmentRequestType;
import com.arextest.report.model.api.contracts.environment.EnvironmentType;
import com.arextest.report.model.api.contracts.environment.QueryEnvsByWorkspaceRequestType;
import com.arextest.report.model.api.contracts.environment.QueryEnvsByWorkspaceResponseType;
import com.arextest.report.model.api.contracts.environment.RemoveEnvironmentRequestType;
import com.arextest.report.model.api.contracts.environment.SaveEnvironmentRequestType;
import com.arextest.report.model.api.contracts.environment.SaveEnvironmentResponseType;
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
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/environment/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EnvironmentController {

    @Resource
    private EnvironmentService environmentService;

    @Resource
    private RolePermission rolePermission;

    @PostMapping("/saveEnvironment")
    @ResponseBody
    public Response saveEnvironment(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody SaveEnvironmentRequestType request) {
        if (request.getEnv() == null) {
            return ResponseUtils.errorResponse("env cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getEnv().getWorkspaceId())) {
            return ResponseUtils.errorResponse("Please provide a workspaceId",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ENVIRONMENT,
                token,
                request.getEnv().getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        if (StringUtils.isEmpty(request.getEnv().getEnvName())) {
            return ResponseUtils.errorResponse("Please provide a environment name",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SaveEnvironmentResponseType response = new SaveEnvironmentResponseType();
            response.setSuccess(environmentService.saveEnvironment(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/removeEnvironment")
    @ResponseBody
    public Response removeEnvironment(@RequestHeader(name = Constants.ACCESS_TOKEN) String token, @RequestBody
            RemoveEnvironmentRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ENVIRONMENT, token, request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("environment id cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(environmentService.removeEnvironment(request.getId()));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/duplicateEnvironment")
    @ResponseBody
    public Response duplicateEnvironment(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @RequestBody DuplicateEnvironmentRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_ENVIRONMENT, token, request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("Environment id cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("Workspace id cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            SuccessResponseType response = new SuccessResponseType();
            response.setSuccess(environmentService.duplicateEnvironment(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/queryEnvsByWorkspace")
    @ResponseBody
    public Response queryEnvsByWorkspace(@RequestBody QueryEnvsByWorkspaceRequestType request) {
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("workspace id cannot be empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            QueryEnvsByWorkspaceResponseType response = new QueryEnvsByWorkspaceResponseType();
            List<EnvironmentType> envs = environmentService.queryEnvsByWorkspace(request);
            response.setEnvironments(envs);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }
}
