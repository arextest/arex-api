package com.arextest.web.api.service.controller.config;

import com.arextest.common.annotation.AppAuth;
import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dto.AbstractMultiEnvConfiguration;
import com.arextest.config.model.dto.ModifyType;
import com.arextest.web.core.business.config.MultiEnvConfigurableHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Getter
public abstract class AbstractMultiEnvConfigController<T extends AbstractMultiEnvConfiguration<T>>
    extends AbstractConfigurableController<T> {

  protected MultiEnvConfigurableHandler<T> multiEnvConfigurableHandler;

  protected AbstractMultiEnvConfigController(MultiEnvConfigurableHandler<T> configurableHandler) {
    super(configurableHandler);
    this.multiEnvConfigurableHandler = configurableHandler;
  }

  @PostMapping("/modify/{modifyType}")
  @ResponseBody
  @AppAuth
  @Override
  public Response modify(@PathVariable ModifyType modifyType, @RequestBody T configuration)
      throws Exception {
    Response response;
    if (modifyType == ModifyType.UPDATE_MULTI_ENV) {
      configuration.validateEnvConfigs();
      response = ResponseUtils.successResponse(
          getMultiEnvConfigurableHandler().editMultiEnvList(configuration));
    } else {
      response = super.modify(modifyType, configuration);
    }
    LOGGER.info("[[title=config.modify]]modify config, modifyType: {}, request: {}", configuration, response);
    return response;
  }
}
