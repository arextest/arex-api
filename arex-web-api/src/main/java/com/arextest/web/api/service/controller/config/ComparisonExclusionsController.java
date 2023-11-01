package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonExclusionsConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonExclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import java.util.List;
import javax.annotation.Resource;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
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

  @Getter
  @Resource
  ComparisonExclusionsConfigurableHandler comparisonExclusionsConfigurableHandler;

  public ComparisonExclusionsController(
      @Autowired ConfigurableHandler<ComparisonExclusionsConfiguration> configurableHandler) {
    super(configurableHandler);
  }

  @Deprecated
  @RequestMapping("/useResultAsList")
  @ResponseBody
  public final Response useResultList(@RequestParam String appId,
      @RequestParam(required = false) String operationId,
      @RequestParam(defaultValue = "false") Boolean filterExpired) {
    if (StringUtils.isEmpty(appId)) {
      return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
    }
    List<ComparisonExclusionsConfiguration> configs =
        getComparisonExclusionsConfigurableHandler().useResultAsList(appId, operationId);
    if (filterExpired && CollectionUtils.isNotEmpty(configs)) {
      configs.removeIf(
          config -> getComparisonExclusionsConfigurableHandler().removeDetailsExpired(config));
    }
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
  public final Response queryByInterfaceId(@RequestParam String interfaceId,
      @RequestParam(defaultValue = "false") Boolean filterExpired) {
    if (StringUtils.isEmpty(interfaceId)) {
      return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
    }
    List<ComparisonExclusionsConfiguration> configs =
        getComparisonExclusionsConfigurableHandler().queryByInterfaceId(interfaceId);
    if (filterExpired && CollectionUtils.isNotEmpty(configs)) {
      configs.removeIf(
          config -> getComparisonExclusionsConfigurableHandler().removeDetailsExpired(config));
    }
    return ResponseUtils.successResponse(configs);
  }

  @PostMapping("/queryComparisonConfig")
  @ResponseBody
  public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
    List<ComparisonExclusionsConfiguration> configs = getComparisonExclusionsConfigurableHandler().queryComparisonConfig(
        request.getAppId(), request.getOperationId(), request.getOperationType(),
        request.getOperationName());
    if (Boolean.TRUE.equals(request.getFilterExpired()) && CollectionUtils.isNotEmpty(configs)) {
      configs.removeIf(
          config -> getComparisonExclusionsConfigurableHandler().removeDetailsExpired(config));
    }
    return ResponseUtils.successResponse(configs);
  }

}