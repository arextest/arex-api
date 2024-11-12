package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonScriptConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonScriptConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonRequestType;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import jakarta.annotation.Resource;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/config/comparison/script")
public class ComparisonScriptConfigurableController extends
    AbstractConfigurableController<ComparisonScriptConfiguration> {

  @Resource
  ComparisonScriptConfigurableHandler scriptConfigurableHandler;

  protected ComparisonScriptConfigurableController(
      ConfigurableHandler<ComparisonScriptConfiguration> configurableHandler) {
    super(configurableHandler);
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
    List<ComparisonScriptConfiguration> configs = scriptConfigurableHandler.queryByInterfaceId(
        interfaceId);
    scriptConfigurableHandler.removeDetailsExpired(configs, filterExpired);
    return ResponseUtils.successResponse(configs);
  }

  @PostMapping("/queryComparisonConfig")
  @ResponseBody
  public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
    List<ComparisonScriptConfiguration> configs = scriptConfigurableHandler.queryComparisonConfig(
        request.getAppId(), request.getOperationId(), request.getOperationType(),
        request.getOperationName());
    scriptConfigurableHandler.removeDetailsExpired(configs, request.getFilterExpired());
    return ResponseUtils.successResponse(configs);
  }

  @PostMapping("/pageQueryComparisonConfig")
  @ResponseBody
  public Response pageQueryComparisonConfig(@RequestBody PageQueryComparisonRequestType request) {
    return ResponseUtils.successResponse(
        scriptConfigurableHandler.pageQueryComparisonConfig(request)
    );
  }


}
