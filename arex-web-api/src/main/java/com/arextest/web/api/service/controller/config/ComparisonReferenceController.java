package com.arextest.web.api.service.controller.config;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonReferenceConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonReferenceConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Controller
@RequestMapping("/api/config/comparison/reference")
public class ComparisonReferenceController extends AbstractConfigurableController<ComparisonReferenceConfiguration> {
    @Resource
    ComparisonReferenceConfigurableHandler comparisonReferenceConfigurableHandler;

    protected ComparisonReferenceController(
        @Autowired ConfigurableHandler<ComparisonReferenceConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @Deprecated
    @RequestMapping("/useResultAsList")
    @ResponseBody
    public Response useResultList(@RequestParam String appId,
        @RequestParam(required = false) String operationId) {
        if (StringUtils.isEmpty(appId)) {
            return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
        }
        return ResponseUtils
            .successResponse(this.comparisonReferenceConfigurableHandler.useResultAsList(appId, operationId));
    }

    @RequestMapping("/queryByInterfaceId")
    @ResponseBody
    public Response queryByInterfaceId(@RequestParam String interfaceId) {
        if (StringUtils.isEmpty(interfaceId)) {
            return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
        }

        return ResponseUtils
            .successResponse(this.comparisonReferenceConfigurableHandler.queryByInterfaceId(interfaceId));
    }

    @PostMapping("/queryComparisonConfig")
    @ResponseBody
    public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
        return ResponseUtils.successResponse(this.comparisonReferenceConfigurableHandler.queryComparisonConfig(
            request.getAppId(), request.getOperationId(), request.getOperationType(), request.getOperationName()));
    }

}
