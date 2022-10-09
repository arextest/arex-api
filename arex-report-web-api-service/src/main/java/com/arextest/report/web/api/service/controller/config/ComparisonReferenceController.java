package com.arextest.report.web.api.service.controller.config;

import com.arextest.report.core.business.config.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.config.replay.ComparisonReferenceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Controller
@RequestMapping("/api/config/comparison/reference")
public class ComparisonReferenceController extends AbstractConfigurableController<ComparisonReferenceConfiguration> {
    protected ComparisonReferenceController(@Autowired ConfigurableHandler<ComparisonReferenceConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}
