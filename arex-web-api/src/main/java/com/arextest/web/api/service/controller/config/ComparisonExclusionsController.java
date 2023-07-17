package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonExclusionsConfigurableHandler;
import com.arextest.web.model.contract.contracts.common.enums.ModifyType;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/api/config/comparison/exclusions")
public class ComparisonExclusionsController extends AbstractConfigurableController<ComparisonExclusionsConfiguration> {
    public ComparisonExclusionsController(
        @Autowired ConfigurableHandler<ComparisonExclusionsConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @Resource
    ComparisonExclusionsConfigurableHandler comparisonExclusionsConfigurableHandler;

    @Deprecated
    @RequestMapping("/useResultAsList")
    @ResponseBody
    public final Response useResultList(@RequestParam String appId,
        @RequestParam(required = false) String operationId) {
        if (StringUtils.isEmpty(appId)) {
            return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
        }
        return ResponseUtils
            .successResponse(this.comparisonExclusionsConfigurableHandler.useResultAsList(appId, operationId));
    }

    /**
     * query config which is compareConfig =1
     * 
     * @param interfaceId
     * @return
     */
    @RequestMapping("/queryByInterfaceId")
    @ResponseBody
    public final Response queryByInterfaceId(@RequestParam String interfaceId) {
        if (StringUtils.isEmpty(interfaceId)) {
            return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
        }
        return ResponseUtils
            .successResponse(this.comparisonExclusionsConfigurableHandler.queryByInterfaceId(interfaceId));
    }

    @PostMapping("/queryComparisonConfig")
    @ResponseBody
    public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
        return ResponseUtils.successResponse(this.comparisonExclusionsConfigurableHandler.queryComparisonConfig(
            request.getAppId(), request.getOperationId(), request.getOperationType(), request.getOperationName()));
    }

}