package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.LabelService;
import com.arextest.report.core.business.filesystem.RolePermission;
import com.arextest.report.model.api.contracts.SuccessResponseType;
import com.arextest.report.model.api.contracts.label.QueryLabelsByWorkspaceIdRequestType;
import com.arextest.report.model.api.contracts.label.QueryLabelsByWorkspaceIdResponseType;
import com.arextest.report.model.api.contracts.label.RemoveLabelRequestType;
import com.arextest.report.model.api.contracts.label.SaveLabelRequestType;
import com.arextest.report.model.dao.mongodb.LabelCollection;
import com.arextest.report.model.dto.LabelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author b_yu
 * @since 2022/11/17
 */
@Slf4j
@RestController
@RequestMapping("/api/label/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LabelController {

    @Resource
    private RolePermission rolePermission;

    @Resource
    private LabelService labelService;

    @PostMapping("/save")
    public Response saveLabel(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @Valid @RequestBody SaveLabelRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_LABEL, token, request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(labelService.saveLabel(request));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/remove")
    public Response removeLabel(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @Valid @RequestBody RemoveLabelRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_LABEL, token, request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(labelService.removeLabel(request));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/queryLabelsByWorkspaceId")
    public Response queryLabelsByWorkspaceId(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
            @Valid @RequestBody QueryLabelsByWorkspaceIdRequestType request) {
        if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_LABEL, token, request.getWorkspaceId())) {
            return ResponseUtils.errorResponse(Constants.NO_PERMISSION, ResponseCode.AUTHENTICATION_FAILED);
        }
        QueryLabelsByWorkspaceIdResponseType response = new QueryLabelsByWorkspaceIdResponseType();
        response.setLabels(labelService.queryLabelsByWorkspaceId(request.getWorkspaceId()));
        return ResponseUtils.successResponse(response);
    }
}
