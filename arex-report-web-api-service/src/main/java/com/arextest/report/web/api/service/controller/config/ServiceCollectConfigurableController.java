package com.arextest.report.web.api.service.controller.config;

import com.arextest.report.core.business.config.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.config.record.ServiceCollectConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/serviceCollect")
public final class ServiceCollectConfigurableController extends AbstractConfigurableController<ServiceCollectConfiguration> {
    public ServiceCollectConfigurableController(@Autowired ConfigurableHandler<ServiceCollectConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}
