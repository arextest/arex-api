package com.arextest.web.api.service.controller.config;

import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.record.DynamicClassConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/dynamicClass")
public final class DynamicClassConfigurableController extends AbstractConfigurableController<DynamicClassConfiguration> {
    public DynamicClassConfigurableController(@Autowired ConfigurableHandler<DynamicClassConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}
