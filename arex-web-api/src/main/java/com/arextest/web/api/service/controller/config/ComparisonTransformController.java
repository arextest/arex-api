package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonTransformConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonTransformConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/config/comparison/transform")
public class ComparisonTransformController extends
    AbstractConfigurableController<ComparisonTransformConfiguration> {

  ComparisonTransformConfigurableHandler transformHandler;

  public ComparisonTransformController(
      ConfigurableHandler<ComparisonTransformConfiguration> configurableHandler,
      ComparisonTransformConfigurableHandler transformHandler) {
    super(configurableHandler);
    this.transformHandler = transformHandler;
  }

  @Deprecated
  @GetMapping("/useResultAsList")
  @ResponseBody
  public final Response useResultList(@RequestParam String appId,
      @RequestParam(required = false) String operationId,
      @RequestParam(defaultValue = "false") Boolean filterExpired) {
    if (StringUtils.isEmpty(appId)) {
      return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
    }
    List<ComparisonTransformConfiguration> configs =
        transformHandler.useResultAsList(appId, operationId);
    transformHandler.removeDetailsExpired(configs, filterExpired);
    return ResponseUtils.successResponse(configs);
  }

  /**
   * query config which is compareConfig =1
   *
   * @param interfaceId
   * @return
   */
  @GetMapping("/queryByInterfaceId")
  @ResponseBody
  public final Response queryByInterfaceId(@RequestParam String interfaceId,
      @RequestParam(defaultValue = "false") Boolean filterExpired) {
    if (StringUtils.isEmpty(interfaceId)) {
      return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
    }
    List<ComparisonTransformConfiguration> configs =
        transformHandler.queryByInterfaceId(interfaceId);
    transformHandler.removeDetailsExpired(configs, filterExpired);
    return ResponseUtils.successResponse(configs);
  }

  @PostMapping("/queryComparisonConfig")
  @ResponseBody
  public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
    List<ComparisonTransformConfiguration> configs = transformHandler.
        queryComparisonConfig(request.getAppId(), request.getOperationId(),
            request.getOperationType(), request.getOperationName());
    transformHandler.removeDetailsExpired(configs, request.getFilterExpired());
    return ResponseUtils.successResponse(configs);
  }

  @GetMapping("/getTransformMethod")
  @ResponseBody
  public final Response getTransformMethod(@RequestParam String appId) {
    if (StringUtils.isEmpty(appId)) {
      return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
    }
    return ResponseUtils.successResponse(transformHandler.getTransformMethodList());
  }

}