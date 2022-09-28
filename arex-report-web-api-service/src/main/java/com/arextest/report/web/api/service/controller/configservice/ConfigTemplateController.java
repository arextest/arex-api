package com.arextest.report.web.api.service.controller.configservice;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.configservice.ConfigService;
import com.arextest.report.model.api.contracts.configservice.PushConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.PushConfigTemplateResponseType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateRequestType;
import com.arextest.report.model.api.contracts.configservice.QueryConfigTemplateResponseType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by rchen9 on 2022/9/27.
 */
@Controller
@RequestMapping("/api/config/yamlTemplate")
public class ConfigTemplateController {

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
