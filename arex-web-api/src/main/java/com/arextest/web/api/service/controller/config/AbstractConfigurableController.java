package com.arextest.web.api.service.controller.config;

import com.arextest.common.annotation.AppAuth;
import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dto.AbstractConfiguration;
import com.arextest.config.model.dto.ModifyType;
import com.arextest.web.core.business.config.ConfigurableHandler;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jmo
 * @since 2022/1/22
 */
public abstract class AbstractConfigurableController<T extends AbstractConfiguration> {

  @Getter
  protected final ConfigurableHandler<T> configurableHandler;

  protected AbstractConfigurableController(ConfigurableHandler<T> configurableHandler) {
    this.configurableHandler = configurableHandler;
  }

  @GetMapping("/useResult/appId/{appId}")
  @ResponseBody
  public final Response useResult(@PathVariable String appId) {
    if (StringUtils.isEmpty(appId)) {
      return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
    }
    return ResponseUtils.successResponse(getConfigurableHandler().useResult(appId));
  }

  @GetMapping("/useResultAsList/appId/{appId}")
  @ResponseBody
  public final Response useResultList(@PathVariable String appId) {
    if (StringUtils.isEmpty(appId)) {
      return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
    }
    return ResponseUtils.successResponse(getConfigurableHandler().useResultAsList(appId));
  }

  @GetMapping("/editList/appId/{appId}")
  @ResponseBody
  public final Response editList(@PathVariable String appId) {
    if (StringUtils.isEmpty(appId)) {
      return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
    }
    return ResponseUtils.successResponse(getConfigurableHandler().editList(appId));
  }

  @PostMapping("/modify/{modifyType}")
  @ResponseBody
  @AppAuth
  public Response modify(@PathVariable ModifyType modifyType, @RequestBody T configuration)
      throws Exception {
    if (modifyType == ModifyType.INSERT) {
      configuration.validParameters();
      return ResponseUtils.successResponse(getConfigurableHandler().insert(configuration));
    }
    if (modifyType == ModifyType.UPDATE) {
      return ResponseUtils.successResponse(getConfigurableHandler().update(configuration));
    }
    if (modifyType == ModifyType.REMOVE) {
      return ResponseUtils.successResponse(getConfigurableHandler().remove(configuration));
    }
    return ResponseUtils.resourceNotFoundResponse();
  }

  @PostMapping("/batchModify/{modifyType}")
  @ResponseBody
  @AppAuth
  public final Response batchModify(@PathVariable ModifyType modifyType,
      @RequestBody List<T> configuration)
      throws Exception {
    if (modifyType == ModifyType.INSERT) {
      for (T item : configuration) {
        item.validParameters();
      }
      return ResponseUtils.successResponse(getConfigurableHandler().insertList(configuration));
    }
    if (modifyType == ModifyType.REMOVE) {
      return ResponseUtils.successResponse(getConfigurableHandler().removeList(configuration));
    }
    return ResponseUtils.resourceNotFoundResponse();
  }

}
