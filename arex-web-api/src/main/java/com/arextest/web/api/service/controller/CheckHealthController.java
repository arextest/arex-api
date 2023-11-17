package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/vi/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CheckHealthController {
  @Value("${pom.version}")
  private String VERSION;

  @GetMapping("/health")
  @ResponseBody
  public Response checkHealth() {

    return ResponseUtils.successResponse(VERSION);
  }
}
