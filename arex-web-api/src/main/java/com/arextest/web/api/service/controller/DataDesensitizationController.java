package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.DesensitizationService;
import com.arextest.web.model.contract.contracts.datadesensitization.DeleteDesensitizationJarRequestType;
import com.arextest.web.model.contract.contracts.datadesensitization.UploadDesensitizationJarRequestType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Created by Qzmo on 2023/8/16
 */
@Controller
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api/desensitization/", produces = "application/json;charset=UTF-8")
public class DataDesensitizationController {
    @Resource
    private DesensitizationService desensitizationService;

    @PostMapping("/saveJar")
    @ResponseBody
    public Response saveJar(@Valid @RequestBody UploadDesensitizationJarRequestType request) {
        try {
            desensitizationService.saveJar(request.getJarUrl(), request.getRemark());
            return ResponseUtils.successResponse(true);
        } catch (DesensitizationService.DesensitizationJarValidationException e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        } catch (Exception e) {
            LOGGER.error("saveJar error", e);
            return ResponseUtils.errorResponse("Internal server err", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/saveJar")
    @ResponseBody
    public Response deleteJar(@Valid @RequestBody DeleteDesensitizationJarRequestType request) {
        try {
            desensitizationService.deleteJar(request.getId());
            return ResponseUtils.successResponse(true);
        } catch (Exception e) {
            LOGGER.error("delete jar error", e);
            return ResponseUtils.errorResponse("Internal server err", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/listJar")
    @ResponseBody
    public Response listJar() {
        try {
            return ResponseUtils.successResponse(desensitizationService.listAllJars());
        } catch (Exception e) {
            LOGGER.error("delete jar error", e);
            return ResponseUtils.errorResponse("Internal server err", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }
}
