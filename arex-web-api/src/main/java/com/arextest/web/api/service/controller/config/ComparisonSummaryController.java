package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.replay.ComparisonSummaryService;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonSummaryConfiguration;
import com.arextest.web.model.contract.contracts.config.replay.QueryCompareConfigRequestType;
import com.arextest.web.model.contract.contracts.config.replay.QueryConfigOfCategoryRequestType;
import com.arextest.web.model.contract.contracts.config.replay.ReplayCompareConfig;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by rchen9 on 2023/1/9.
 */
@Controller
@RequestMapping("/api/config/comparison/summary")
public class ComparisonSummaryController {

  @Resource
  ComparisonSummaryService comparisonSummaryService;

  @RequestMapping("/queryByInterfaceId")
  @ResponseBody
  public final Response queryByInterfaceId(@RequestParam String interfaceId) {
    if (StringUtils.isEmpty(interfaceId)) {
      return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
    }
    ComparisonSummaryConfiguration comparisonDetailsSummary =
        comparisonSummaryService.getComparisonDetailsSummary(interfaceId);

    return ResponseUtils.successResponse(comparisonDetailsSummary);
  }

  @GetMapping("/queryByAppId/{appId}")
  @ResponseBody
  public Response queryConfigByAppId(@PathVariable String appId) {
    ReplayCompareConfig replayCompareConfig = comparisonSummaryService.getReplayComparisonConfig(
        appId);
    return ResponseUtils.successResponse(replayCompareConfig);
  }

  @PostMapping("/queryConfigOfCategory")
  @ResponseBody
  public Response queryConfigOfCategory(@RequestBody QueryConfigOfCategoryRequestType request) {
    return ResponseUtils.successResponse(
        comparisonSummaryService.queryConfigOfCategory(request)
    );
  }

  @PostMapping("/queryCompareConfig")
  @ResponseBody
  public Response queryCompareConfig(@RequestBody QueryCompareConfigRequestType request) {
    ReplayCompareConfig replayCompareConfig = comparisonSummaryService.getReplayComparisonConfig(
        request.getAppId(), request.getOperationName());
    return ResponseUtils.successResponse(replayCompareConfig);
  }

}