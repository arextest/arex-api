package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.config.model.dto.SystemConfiguration;
import com.arextest.config.repository.SystemConfigurationRepository;
import com.arextest.config.repository.impl.SystemConfigurationRepositoryImpl;
import com.arextest.web.core.business.ConfigLoadService;
import com.arextest.web.model.contract.contracts.config.SaveSystemConfigRequestType;
import com.arextest.web.model.contract.contracts.config.SystemConfigWithProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wildeslam.
 * @create 2023/9/26 11:25
 */
@Controller
@RequestMapping("/api/system/config")
@Slf4j
public class SystemConfigController {

  @Resource
  private SystemConfigurationRepository systemConfigurationRepository;

  @Resource
  private ConfigLoadService configLoadService;

  @PostMapping("/save")
  @ResponseBody
  public Response saveSystemConfig(@RequestBody SaveSystemConfigRequestType request) {
    return ResponseUtils.successResponse(systemConfigurationRepository.saveConfig(request.getSystemConfig()));
  }

  @GetMapping("/list")
  @ResponseBody
  public Response listSystemConfig() {
    List<com.arextest.config.model.dto.SystemConfiguration> systemConfigurations = systemConfigurationRepository.getAllSystemConfigList();

    SystemConfiguration systemConfiguration = SystemConfiguration.mergeConfigs(systemConfigurations);
    SystemConfigWithProperties systemConfigWithProperties = this.appendGlobalCompareConfig(configLoadService,
        systemConfiguration);
    return ResponseUtils.successResponse(systemConfigWithProperties);
  }

  @GetMapping("/query/{key}")
  @ResponseBody
  public Response querySystemConfigByKey(@PathVariable String key) {
    SystemConfiguration systemConfiguration = systemConfigurationRepository.getSystemConfigByKey(key);
    return ResponseUtils.successResponse(systemConfiguration);
  }



  private SystemConfigWithProperties appendGlobalCompareConfig(ConfigLoadService configLoadService,
                                                               SystemConfiguration latestSystemConfig) {
    SystemConfigWithProperties systemConfigWithProperties = new SystemConfigWithProperties();
    systemConfigWithProperties.setCallbackUrl(latestSystemConfig.getCallbackUrl());
    systemConfigWithProperties.setDesensitizationJar(latestSystemConfig.getDesensitizationJar());
    systemConfigWithProperties.setRefreshTaskMark(latestSystemConfig.getRefreshTaskMark());
    try {
      systemConfigWithProperties.setCompareNameToLower(
          Boolean.valueOf(configLoadService.getCompareNameToLower("true")));
      systemConfigWithProperties.setCompareNullEqualsEmpty(
          Boolean.valueOf(configLoadService.getCompareNullEqualsEmpty("true")));
      Long ignoredTimePrecisionMillis = Long.valueOf(
          configLoadService.getCompareIgnoredTimePrecisionMillis("2000"));
      systemConfigWithProperties.setCompareIgnoreTimePrecisionMillis(ignoredTimePrecisionMillis);
      systemConfigWithProperties.setIgnoreNodeSet(configLoadService.getIgnoreNodeSet(""));
      systemConfigWithProperties.setSelectIgnoreCompare(
          Boolean.valueOf(configLoadService.getCompareSelectIgnoreCompare("true")));
      systemConfigWithProperties.setOnlyCompareCoincidentColumn(
          Boolean.valueOf(configLoadService.getCompareOnlyCompareCoincidentColumn("true")));
      systemConfigWithProperties.setUuidIgnore(
          Boolean.valueOf(configLoadService.getCompareUuidIgnore("true")));
      systemConfigWithProperties.setIpIgnore(
          Boolean.valueOf(configLoadService.getCompareIpIgnore("true")));
    } catch (RuntimeException e) {
      LOGGER.error("getCompareIgnoredTimePrecisionMillis error", e);
    }
    return systemConfigWithProperties;
  }
}