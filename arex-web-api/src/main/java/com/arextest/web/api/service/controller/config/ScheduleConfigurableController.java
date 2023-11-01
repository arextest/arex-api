package com.arextest.web.api.service.controller.config;

import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ScheduleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/schedule")
public class ScheduleConfigurableController extends
    AbstractConfigurableController<ScheduleConfiguration> {

  public ScheduleConfigurableController(
      @Autowired ConfigurableHandler<ScheduleConfiguration> configurableHandler) {
    super(configurableHandler);
  }
}
