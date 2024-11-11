package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonExclusionsConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonRequestType;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/config/comparison/exclusions")
public class ComparisonExclusionsController extends
    AbstractConfigurableController<ComparisonExclusionsConfiguration> {

  @Autowired
  ComparisonExclusionsConfigurableHandler exclusionsHandler;

  protected ComparisonExclusionsController(
      ConfigurableHandler<ComparisonExclusionsConfiguration> configurableHandler) {
    super(configurableHandler);
  }

  @Deprecated
  @RequestMapping("/useResultAsList")
  @ResponseBody
  public Response useResultList(@RequestParam String appId,
      @RequestParam(required = false) String operationId,
      @RequestParam(defaultValue = "false") Boolean filterExpired) {
    if (StringUtils.isEmpty(appId)) {
      return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
    }
    List<ComparisonExclusionsConfiguration> configs = exclusionsHandler.useResultAsList(appId,
        operationId);
    exclusionsHandler.removeDetailsExpired(configs, filterExpired);
    return ResponseUtils.successResponse(configs);
  }

  /**
   * query config which is compareConfig =1
   *
   * @param interfaceId
   * @return
   */
  @RequestMapping("/queryByInterfaceId")
  @ResponseBody
  public Response queryByInterfaceId(@RequestParam String interfaceId,
      @RequestParam(defaultValue = "false") Boolean filterExpired) {
    if (StringUtils.isEmpty(interfaceId)) {
      return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
    }
    List<ComparisonExclusionsConfiguration> configs = exclusionsHandler.queryByInterfaceId(
        interfaceId);
    exclusionsHandler.removeDetailsExpired(configs, filterExpired);
    return ResponseUtils.successResponse(configs);
  }

  @PostMapping("/queryComparisonConfig")
  @ResponseBody
  public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
    List<ComparisonExclusionsConfiguration> configs = exclusionsHandler.queryComparisonConfig(
        request.getAppId(), request.getOperationId(), request.getOperationType(),
        request.getOperationName());
    exclusionsHandler.removeDetailsExpired(configs, request.getFilterExpired());
    return ResponseUtils.successResponse(configs);
  }

  @PostMapping("/pageQueryComparisonConfig")
  @ResponseBody
  public Response pageQueryComparisonConfig(@RequestBody PageQueryComparisonRequestType request) {
    return ResponseUtils.successResponse(
        exclusionsHandler.pageQueryComparisonConfig(request)
    );
  }


}