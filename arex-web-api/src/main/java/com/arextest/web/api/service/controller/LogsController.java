package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.LogsService;
import com.arextest.web.model.contract.contracts.QueryLogsRequestType;
import com.arextest.web.model.contract.contracts.QueryLogsResponseType;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author b_yu
 * @since 2023/2/10
 */
@Controller
@RequestMapping("/api/logs/")
public class LogsController {

  @Resource
  private LogsService logsService;

  @PostMapping("/query")
  @ResponseBody
  public Response queryLogs(@RequestBody QueryLogsRequestType request) {
    QueryLogsResponseType response = new QueryLogsResponseType();
    response.setLogs(logsService.queryLogs(request));
    return ResponseUtils.successResponse(response);
  }
}
