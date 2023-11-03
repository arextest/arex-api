package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.config.model.dto.ModifyType;
import com.arextest.config.model.dto.application.InstancesConfiguration;
import com.arextest.web.core.business.config.application.ApplicationInstancesConfigurableHandler;
import com.arextest.web.core.business.config.record.ServiceCollectConfigurableHandler;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by rchen9 on 2022/9/30.
 */
@Controller
@RequestMapping("/api/config/applicationInstances")
public class ApplicationInstancesConfigurableController extends
    AbstractConfigurableController<InstancesConfiguration> {

  @Resource
  private ServiceCollectConfigurableHandler serviceCollectConfigurableHandler;

  public ApplicationInstancesConfigurableController(
      @Autowired ApplicationInstancesConfigurableHandler configurableHandler) {
    super(configurableHandler);
  }

  @Override
  @ResponseBody
  public Response modify(@PathVariable ModifyType modifyType,
      @RequestBody InstancesConfiguration configuration)
      throws Exception {
    serviceCollectConfigurableHandler.updateServiceCollectTime(configuration.getAppId());

    return super.modify(modifyType, configuration);
  }
}