package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.ConfigLoadService;
import com.arextest.web.core.repository.SystemConfigRepository;
import com.arextest.web.model.contract.contracts.config.SaveSystemConfigRequestType;
import com.arextest.web.model.contract.contracts.config.SystemConfig;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wildeslam.
 * @create 2023/9/26 11:25
 */
@Controller
@RequestMapping("/api/system/config")
@Slf4j
public class SystemConfigController {

  @Resource
  private SystemConfigRepository systemConfigRepository;

  @Resource
  ConfigLoadService configLoadService;

  @PostMapping("/save")
  @ResponseBody
  public Response saveSystemConfig(@RequestBody SaveSystemConfigRequestType request) {
    return ResponseUtils.successResponse(
        systemConfigRepository.saveConfig(request.getSystemConfig()));
  }

  @GetMapping("/list")
  @ResponseBody
  public Response listSystemConfig() {
    SystemConfig latestSystemConfig = systemConfigRepository.getLatestSystemConfig();
    latestSystemConfig.setCompareNameToLower(
        Boolean.valueOf(configLoadService.getCompareNameToLower("true")));
    latestSystemConfig.setCompareNullEqualsEmpty(
        Boolean.valueOf(configLoadService.getCompareNullEqualsEmpty("true")));
    try {
      Long ignoredTimePrecisionMillis = Long.valueOf(
          configLoadService.getCompareIgnoredTimePrecisionMillis("2000"));
      latestSystemConfig.setCompareIgnoreTimePrecisionMillis(ignoredTimePrecisionMillis);
    } catch (RuntimeException e) {
      LOGGER.error("getCompareIgnoredTimePrecisionMillis error", e);
    }
    return ResponseUtils.successResponse(systemConfigRepository.getLatestSystemConfig());
  }
}
