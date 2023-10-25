package com.arextest.web.api.service.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.DesensitizationService;
import com.arextest.web.model.contract.contracts.datadesensitization.DeleteDesensitizationJarRequestType;
import com.arextest.web.model.contract.contracts.datadesensitization.UploadDesensitizationJarRequestType;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Qzmo on 2023/8/16
 */
@SuppressWarnings("squid:S5122")
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
        return ResponseUtils.successResponse(desensitizationService.saveJar(request.getJarUrl(), request.getRemark()));
    }

    @PostMapping("/deleteJar")
    @ResponseBody
    public Response deleteJar(@Valid @RequestBody DeleteDesensitizationJarRequestType request) {
        return ResponseUtils.successResponse(desensitizationService.deleteJar(request.getId()));
    }

    @PostMapping("/listJar")
    @ResponseBody
    public Response listJar() {
        return ResponseUtils.successResponse(desensitizationService.listAllJars());
    }
}
