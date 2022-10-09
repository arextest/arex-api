package com.arextest.report.web.api.service.controller.config;


import com.arextest.report.core.business.config.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.config.replay.ComparisonInclusionsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/config/comparison/inclusions")
public class ComparisonInclusionsController extends AbstractConfigurableController<ComparisonInclusionsConfiguration> {
    public ComparisonInclusionsController(
            @Autowired ConfigurableHandler<ComparisonInclusionsConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}
