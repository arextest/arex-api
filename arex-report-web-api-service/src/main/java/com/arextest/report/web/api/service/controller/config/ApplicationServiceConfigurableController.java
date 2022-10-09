package com.arextest.report.web.api.service.controller.config;

import com.arextest.report.core.business.config.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.config.application.ApplicationServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/applicationService")
public final class ApplicationServiceConfigurableController extends AbstractConfigurableController<ApplicationServiceConfiguration> {
    public ApplicationServiceConfigurableController(@Autowired ConfigurableHandler<ApplicationServiceConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}
