package com.arextest.web.api.service.controller.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arextest.config.model.dto.record.ServiceCollectConfiguration;
import com.arextest.web.core.business.config.MultiEnvConfigurableHandler;

/**
 * @author jmo
 * @since 2022/1/22
 */
@Controller
@RequestMapping("/api/config/serviceCollect")
public class ServiceCollectConfigurableController extends
    AbstractMultiEnvConfigController<ServiceCollectConfiguration> {

  public ServiceCollectConfigurableController(
      @Autowired MultiEnvConfigurableHandler<ServiceCollectConfiguration> configurableHandler) {
    super(configurableHandler);
  }
}
