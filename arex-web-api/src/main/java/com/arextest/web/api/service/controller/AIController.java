package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.ai.AI;
import com.arextest.web.model.contract.contracts.vertexai.GenReq;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: QizhengMo
 * @date: 2024/3/25 15:15
 */
@Slf4j
@Controller
@RequestMapping("/ai/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AIController {


  @PostMapping("/generateTestScript")
  @ResponseBody
  public Response generateTestScript(@RequestBody GenReq req) {
    return ResponseUtils.successResponse(AI.generateScripts(req));
  }
}
