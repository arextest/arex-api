package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.application.ApplicationOperationConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.application.ApplicationOperationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by rchen9 on 2022/9/30.
 */
@Controller
@RequestMapping("/api/config/applicationOperation")
public class ApplicationOperationConfigurableController extends AbstractConfigurableController<ApplicationOperationConfiguration> {

    public ApplicationOperationConfigurableController(@Autowired ApplicationOperationConfigurableHandler configurableHandler) {
        super(configurableHandler);
    }

    @Autowired
    ApplicationOperationConfigurableHandler applicationOperationConfigurableHandler;

    @GetMapping("/useResult/operationId/{operationId}")
    @ResponseBody
    public final Response useResultById(@PathVariable String operationId) {
        if (StringUtils.isEmpty(operationId)) {
            return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
        }
        return ResponseUtils.successResponse(this.applicationOperationConfigurableHandler.useResultByOperationId(operationId));
    }
}