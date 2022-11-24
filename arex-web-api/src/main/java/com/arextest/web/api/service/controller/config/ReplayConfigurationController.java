package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.replay.ReplayConfigurableService;
import com.arextest.web.model.contract.contracts.config.replay.ReplayConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by rchen9 on 2022/11/7.
 */
@Controller
@RequestMapping("/api/config/replay")
public class ReplayConfigurationController {

    @Autowired
    ReplayConfigurableService replayConfigurableService;

    @GetMapping("/queryConfig/appId/{appId}")
    @ResponseBody
    public Response queryConfig(@PathVariable String appId) {
        ReplayConfiguration replayConfiguration = replayConfigurableService.queryConfig(appId);
        return ResponseUtils.successResponse(replayConfiguration);
    }


}
