package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.configservice.ConfigService;
import com.arextest.report.model.api.contracts.configservice.PushConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.PushConfigTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateResponseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Slf4j
@Controller
@RequestMapping("/api/config/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConfigController {

    @Resource
    private ConfigService configService;

    @PostMapping("/queryConfigTemplate")
    @ResponseBody
    public Response queryConfigTemplate(@RequestBody QueryConfigTemplateRequestType request) {
        if (StringUtils.isEmpty(request.getAppId())) {
            return ResponseUtils.errorResponse("appId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        QueryConfigTemplateResponseType response = configService.queryConfigTemplate(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/pushConfigTemplate")
    @ResponseBody
    public Response pushConfigTemplate(@RequestBody PushConfigTemplateRequestType request) {
        if (StringUtils.isEmpty(request.getAppId())) {
            return ResponseUtils.errorResponse("appId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        PushConfigTemplateResponseType response = configService.pushConfigTemplate(request);
        return ResponseUtils.successResponse(response);
    }


}
