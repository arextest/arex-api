package com.arextest.report.web.api.service.controller.configservice;

import com.arextest.report.core.business.configservice.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.configservice.replay.ComparisonListSortConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by rchen9 on 2022/9/16.
 */
@Controller
@RequestMapping("/api/config/comparison/listsort")
public class ComparisonListSortController extends AbstractConfigurableController<ComparisonListSortConfiguration> {
    public ComparisonListSortController(
            @Autowired ConfigurableHandler<ComparisonListSortConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}
