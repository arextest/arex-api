package com.arextest.report.web.api.service.controller.configservice;


import com.arextest.report.core.business.configservice.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.configservice.replay.ComparisonExclusionsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/config/comparison/exclusions")
public class ComparisonExclusionsController extends AbstractConfigurableController<ComparisonExclusionsConfiguration> {
    public ComparisonExclusionsController(
            @Autowired ConfigurableHandler<ComparisonExclusionsConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}