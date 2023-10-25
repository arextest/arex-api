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
import com.arextest.web.core.business.config.replay.ComparisonIgnoreCategoryConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:54
 */

@Controller
@RequestMapping("/api/config/comparison/ignoreCategory")
public class ComparisonIgnoreCategoryController
    extends AbstractConfigurableController<ComparisonIgnoreCategoryConfiguration> {

    @Resource
    ComparisonIgnoreCategoryConfigurableHandler configurableHandler;

    protected ComparisonIgnoreCategoryController(
        @Autowired ConfigurableHandler<ComparisonIgnoreCategoryConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    /**
     * query config which is compareConfig =1
     */
    @RequestMapping("/queryByInterfaceId")
    @ResponseBody
    public final Response queryByInterfaceId(@RequestParam String interfaceId) {
        if (StringUtils.isEmpty(interfaceId)) {
            return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
        }
        return ResponseUtils.successResponse(this.configurableHandler.queryByInterfaceId(interfaceId));
    }

    @PostMapping("/queryComparisonConfig")
    @ResponseBody
    public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
        return ResponseUtils.successResponse(this.configurableHandler.queryComparisonConfig(request.getAppId(),
            request.getOperationId(), request.getOperationType(), request.getOperationName()));
    }
}
