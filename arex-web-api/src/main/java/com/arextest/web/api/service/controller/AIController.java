package com.arextest.web.api.service.controller;

import com.arextest.web.model.dto.vertexai.TestScriptGenRes;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.ai.AIProvider;
import com.arextest.web.model.contract.contracts.vertexai.GenReq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: QizhengMo
 * @date: 2024/3/25 15:15
 */
@Slf4j
@Controller
@RequestMapping("/api/ai/")
@CrossOrigin(origins = "*", maxAge = 3600)
@ConditionalOnBean(AIProvider.class)
@RequiredArgsConstructor
public class AIController {
  private final List<AIProvider> providers;

  @PostMapping("/generateTestScript")
  @ResponseBody
  public Response generateTestScript(@RequestBody GenReq req) {
    return ResponseUtils.successResponse(getProvider(req.getModelName()).generateScripts(req));
  }

  private AIProvider getProvider(String modelName) {
    Optional<AIProvider> provider = providers
        .stream()
        .filter(p -> p.getModelInfo().getModelName().equals(modelName))
        .findFirst();
    return provider.orElse(providers.get(0));
  }
}
