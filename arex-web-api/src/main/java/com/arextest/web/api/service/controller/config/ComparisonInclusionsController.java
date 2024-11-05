package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.ConfigurableHandler;
import com.arextest.web.core.business.config.replay.ComparisonInclusionsConfigurableHandler;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonInclusionsConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.PageQueryComparisonRequestType;
import com.arextest.web.model.contract.contracts.config.replay.QueryComparisonRequestType;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/config/comparison/inclusions")
public class ComparisonInclusionsController extends
    AbstractConfigurableController<ComparisonInclusionsConfiguration> {

  @Getter
  @Resource
  ComparisonInclusionsConfigurableHandler comparisonInclusionsConfigurableHandler;

  public ComparisonInclusionsController(
      @Autowired ConfigurableHandler<ComparisonInclusionsConfiguration> configurableHandler) {
    super(configurableHandler);
  }

  @Deprecated
  @RequestMapping("/useResultAsList")
  @ResponseBody
  public final Response useResultList(@RequestParam String appId,
      @RequestParam(required = false) String operationId) {
    if (StringUtils.isEmpty(appId)) {
      return InvalidResponse.REQUESTED_APP_ID_IS_EMPTY;
    }
    return ResponseUtils
        .successResponse(
            getComparisonInclusionsConfigurableHandler().useResultAsList(appId, operationId));
  }

  @RequestMapping("/queryByInterfaceId")
  @ResponseBody
  public final Response queryByInterfaceId(@RequestParam String interfaceId) {
    if (StringUtils.isEmpty(interfaceId)) {
      return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
    }
    return ResponseUtils
        .successResponse(
            getComparisonInclusionsConfigurableHandler().queryByInterfaceId(interfaceId));
  }

  @PostMapping("/queryComparisonConfig")
  @ResponseBody
  public Response queryComparisonConfig(@RequestBody QueryComparisonRequestType request) {
    return ResponseUtils.successResponse(
        getComparisonInclusionsConfigurableHandler().queryComparisonConfig(
            request.getAppId(), request.getOperationId(), request.getOperationType(),
            request.getOperationName()));
  }

  @PostMapping("/pageQueryComparisonConfig")
  @ResponseBody
  public Response pageQueryComparisonConfig(@RequestBody PageQueryComparisonRequestType request) {
    return ResponseUtils.successResponse(
        comparisonInclusionsConfigurableHandler.pageQueryComparisonConfig(request)
    );
  }

}
