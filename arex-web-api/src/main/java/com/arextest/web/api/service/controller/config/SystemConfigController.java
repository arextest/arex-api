package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.SystemConfigurationService;
import com.arextest.web.model.contract.contracts.config.SaveSystemConfigRequestType;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private SystemConfigurationService systemConfigurationService;


  @PostMapping("/save")
  @ResponseBody
  public Response saveSystemConfig(@RequestBody SaveSystemConfigRequestType request) {
    if (request.getSystemConfig() == null) {
      return ResponseUtils.errorResponse("System config is null",
          ResponseCode.REQUESTED_PARAMETER_INVALID);
    }
    return ResponseUtils.successResponse(
        systemConfigurationService.saveConfig(request.getSystemConfig()));
  }

  @GetMapping("/list")
  @ResponseBody
  public Response listSystemConfig() {
    return ResponseUtils.successResponse(
        systemConfigurationService.listSystemConfig()
    );
  }

  @GetMapping("/query/{key}")
  @ResponseBody
  public Response querySystemConfigByKey(@PathVariable String key) {
    return ResponseUtils.successResponse(
        systemConfigurationService.getSystemConfigByKey(key)
    );
  }

  @GetMapping("/delete/{key}")
  @ResponseBody
  public Response deleteSystemConfigByKey(@PathVariable String key) {
    return ResponseUtils.successResponse(systemConfigurationService.deleteSystemConfigByKey(key));
  }

}