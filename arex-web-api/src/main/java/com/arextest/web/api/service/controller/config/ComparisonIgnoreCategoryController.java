package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonIgnoreCategoryConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonIgnoreCategoryConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wildeslam.
 * @create 2023/8/18 14:54
 */

@Controller
@RequestMapping("/api/config/comparison/ignoreCategory")
public class ComparisonIgnoreCategoryController
    extends AbstractConfigurableController<ComparisonIgnoreCategoryConfiguration> {

  ComparisonIgnoreCategoryConfigurableHandler ignoreCategoryHandler;

  protected ComparisonIgnoreCategoryController(
      ConfigurableHandler<ComparisonIgnoreCategoryConfiguration> configurableHandler,
      ComparisonIgnoreCategoryConfigurableHandler ignoreCategoryHandler) {
    super(configurableHandler);
    this.ignoreCategoryHandler = ignoreCategoryHandler;
  }

  /**
   * query config which is compareConfig =1
   */
  @RequestMapping("/queryByInterfaceId")
  @ResponseBody
  public final Response queryByInterfaceId(@RequestParam String interfaceId,
      @RequestParam(defaultValue = "false") Boolean filterExpired) {
    if (StringUtils.isEmpty(interfaceId)) {
      return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
    }

    List<ComparisonIgnoreCategoryConfiguration> configs = ignoreCategoryHandler.queryByInterfaceId(
        interfaceId);
    ignoreCategoryHandler.removeDetailsExpired(configs, filterExpired);
    return ResponseUtils.successResponse(configs);
  }

  @PostMapping("/queryComparisonConfig")
  @ResponseBody
  public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
    List<ComparisonIgnoreCategoryConfiguration> configs =
        this.ignoreCategoryHandler.queryComparisonConfig(request.getAppId(),
            request.getOperationId(), request.getOperationType(), request.getOperationName());
    ignoreCategoryHandler.removeDetailsExpired(configs, request.getFilterExpired());
    return ResponseUtils.successResponse(configs);
  }
}
