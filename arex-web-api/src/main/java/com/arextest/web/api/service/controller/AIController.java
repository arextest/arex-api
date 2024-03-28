package com.arextest.web.api.service.controller;

import com.arextest.web.core.business.ai.AIProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.ai.BisonAIProvider;
import com.arextest.web.model.contract.contracts.vertexai.GenReq;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: QizhengMo
 * @date: 2024/3/25 15:15
 */
@Slf4j
@Controller
@RequestMapping("/api/ai/")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class AIController {

  AIProvider provider;

  @PostMapping("/generateTestScript")
  @ResponseBody
  public Response generateTestScript(@RequestBody GenReq req) {
    return ResponseUtils.successResponse(provider.generateScripts(req));
  }
}
