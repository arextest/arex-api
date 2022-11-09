package com.arextest.report.web.api.service.controller.config;


import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.config.replay.ComparisonInclusionsConfigurableHandler;
import com.arextest.report.core.business.config.ConfigurableHandler;
import com.arextest.report.model.api.contracts.config.replay.ComparisonInclusionsConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/api/config/comparison/inclusions")
public class ComparisonInclusionsController extends AbstractConfigurableController<ComparisonInclusionsConfiguration> {
    public ComparisonInclusionsController(
            @Autowired ConfigurableHandler<ComparisonInclusionsConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @Resource
    ComparisonInclusionsConfigurableHandler comparisonInclusionsConfigurableHandler;

    @RequestMapping("/useResultAsList")
    @ResponseBody
    public final Response useResultList(@RequestParam String appId, @RequestParam(required = false) String operationId) {
        if (StringUtils.isEmpty(appId)) {
            return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
        }
        return ResponseUtils.successResponse(this.comparisonInclusionsConfigurableHandler.useResultAsList(appId, operationId));
    }
}
