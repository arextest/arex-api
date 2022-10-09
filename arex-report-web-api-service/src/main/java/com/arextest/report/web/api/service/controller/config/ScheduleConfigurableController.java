package com.arextest.report.web.api.service.controller.config;

import com.arextest.report.core.business.config.handler.ConfigurableHandler;
import com.arextest.report.model.api.contracts.config.replay.ScheduleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/schedule")
public final class ScheduleConfigurableController extends AbstractConfigurableController<ScheduleConfiguration> {
    public ScheduleConfigurableController(@Autowired ConfigurableHandler<ScheduleConfiguration> configurableHandler) {
        super(configurableHandler);
    }
}
