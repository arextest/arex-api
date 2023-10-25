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
import com.arextest.web.core.business.config.replay.ComparisonEncryptionConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonEncryptionConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;

@Controller
@RequestMapping("/api/config/comparison/encryption")
public class ComparisonEncryptionController extends AbstractConfigurableController<ComparisonEncryptionConfiguration> {
    @Resource
    ComparisonEncryptionConfigurableHandler comparisonEncryptionConfigurableHandler;

    public ComparisonEncryptionController(
        @Autowired ConfigurableHandler<ComparisonEncryptionConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @PostMapping("/queryByInterfaceId")
    @ResponseBody
    public Response queryByInterfaceId(@RequestParam String interfaceId) {
        if (StringUtils.isEmpty(interfaceId)) {
            return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
        }
        return ResponseUtils
            .successResponse(this.comparisonEncryptionConfigurableHandler.queryByInterfaceId(interfaceId));
    }

    @PostMapping("/queryComparisonConfig")
    @ResponseBody
    public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
        return ResponseUtils.successResponse(this.comparisonEncryptionConfigurableHandler.queryComparisonConfig(
            request.getAppId(), request.getOperationId(), request.getOperationType(), request.getOperationName()));
    }

}
