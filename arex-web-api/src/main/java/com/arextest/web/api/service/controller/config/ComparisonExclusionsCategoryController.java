package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonExclusionsCategoryConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsCategoryConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:54
 */

@Controller
@RequestMapping("/api/config/comparison/exclusions/category")
public class ComparisonExclusionsCategoryController extends
    AbstractConfigurableController<ComparisonExclusionsCategoryConfiguration> {

    protected ComparisonExclusionsCategoryController(
        @Autowired  ConfigurableHandler<ComparisonExclusionsCategoryConfiguration> configurableHandler) {
        super(configurableHandler);
    }

    @Resource
    ComparisonExclusionsCategoryConfigurableHandler configurableHandler;

    /**
     * query config which is compareConfig =1
     */
    @RequestMapping("/queryByInterfaceId")
    @ResponseBody
    public final Response queryByInterfaceId(@RequestParam String interfaceId) {
        if (StringUtils.isEmpty(interfaceId)) {
            return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
        }
        return ResponseUtils
            .successResponse(this.configurableHandler.queryByInterfaceId(interfaceId));
    }

    @PostMapping("/queryComparisonConfig")
    @ResponseBody
    public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
        return ResponseUtils.successResponse(this.configurableHandler.queryComparisonConfig(
            request.getAppId(), request.getOperationId(), request.getOperationType(), request.getOperationName()));
    }
}
