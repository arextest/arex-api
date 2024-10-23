package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.common.utils.ResponseUtils_New;
import com.arextest.web.common.exception.ArexApiResponseCode;
import com.arextest.web.core.business.LabelService;
import com.arextest.web.core.business.filesystem.RolePermission;
import com.arextest.web.model.contract.contracts.SuccessResponseType;
import com.arextest.web.model.contract.contracts.label.QueryLabelsByWorkspaceIdRequestType;
import com.arextest.web.model.contract.contracts.label.QueryLabelsByWorkspaceIdResponseType;
import com.arextest.web.model.contract.contracts.label.RemoveLabelRequestType;
import com.arextest.web.model.contract.contracts.label.SaveLabelRequestType;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_LABEL, token,
        request.getWorkspaceId())) {
      return ResponseUtils_New.errorResponse(Constants.NO_PERMISSION,
          ArexApiResponseCode.FS_NO_PERMISSION);
    }
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(labelService.saveLabel(request));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/remove")
  public Response removeLabel(@RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody RemoveLabelRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_LABEL, token,
        request.getWorkspaceId())) {
      return ResponseUtils_New.errorResponse(Constants.NO_PERMISSION,
          ArexApiResponseCode.FS_NO_PERMISSION);
    }
    SuccessResponseType response = new SuccessResponseType();
    response.setSuccess(labelService.removeLabel(request));
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryLabelsByWorkspaceId")
  public Response queryLabelsByWorkspaceId(
      @RequestHeader(name = Constants.ACCESS_TOKEN) String token,
      @Valid @RequestBody QueryLabelsByWorkspaceIdRequestType request) {
    if (!rolePermission.checkPermissionByToken(RolePermission.EDIT_LABEL, token,
        request.getWorkspaceId())) {
      return ResponseUtils_New.errorResponse(Constants.NO_PERMISSION,
          ArexApiResponseCode.FS_NO_PERMISSION);
    }
    QueryLabelsByWorkspaceIdResponseType response = new QueryLabelsByWorkspaceIdResponseType();
    response.setLabels(labelService.queryLabelsByWorkspaceId(request.getWorkspaceId()));
    return ResponseUtils.successResponse(response);
  }
}
