package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.repository.SystemConfigRepository;
import com.arextest.web.model.contract.contracts.config.SaveSystemConfigRequestType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author wildeslam.
 * @create 2023/9/26 11:25
 */
@Controller
@RequestMapping("/api/system/config")
public class SystemConfigController {
    @Resource
    private SystemConfigRepository systemConfigRepository;

    @PostMapping("/save")
    @ResponseBody
    public Response saveSystemConfig(@RequestBody SaveSystemConfigRequestType request) {
        if (CollectionUtils.isEmpty(request.getSystemConfigList())) {
            return ResponseUtils.errorResponse("system config list is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        return ResponseUtils.successResponse(systemConfigRepository.saveList(request.getSystemConfigList()));
    }

    @GetMapping("/list")
    @ResponseBody
    public Response listSystemConfig() {
        return ResponseUtils.successResponse(systemConfigRepository.listSystemConfigs());
    }
}
