package com.arextest.report.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.config.QueryYamlTemplateService;
import com.arextest.report.core.business.config.UpdateYamlTemplateService;
import com.arextest.report.model.api.contracts.config.PushYamlTemplateRequestType;
import com.arextest.report.model.api.contracts.config.PushYamlTemplateResponseType;
import com.arextest.report.model.api.contracts.config.QueryYamlTemplateRequestType;
import com.arextest.report.model.api.contracts.config.QueryYamlTemplateResponseType;
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
public class YamlTemplateController {

    @Resource
    private QueryYamlTemplateService queryYamlTemplateService;

    @Resource
    private UpdateYamlTemplateService updateYamlTemplateService;

    @PostMapping("/queryConfigTemplate")
    @ResponseBody
    public Response queryConfigTemplate(@RequestBody QueryYamlTemplateRequestType request) {
        if (StringUtils.isEmpty(request.getAppId())) {
            return ResponseUtils.errorResponse("appId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        QueryYamlTemplateResponseType response = queryYamlTemplateService.queryConfigTemplate(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/pushConfigTemplate")
    @ResponseBody
    public Response pushConfigTemplate(@RequestBody PushYamlTemplateRequestType request) {
        if (StringUtils.isEmpty(request.getAppId())) {
            return ResponseUtils.errorResponse("appId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        PushYamlTemplateResponseType response = updateYamlTemplateService.pushConfigTemplate(request);
        return ResponseUtils.successResponse(response);
    }


}
