package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.ManualReportService;
import com.arextest.web.model.contract.contracts.manualreport.InitManualReportRequestType;
import com.arextest.web.model.contract.contracts.manualreport.InitManualReportResponseType;
import com.arextest.web.model.contract.contracts.manualreport.QueryReportCasesRequestType;
import com.arextest.web.model.contract.contracts.manualreport.QueryReportCasesResponseType;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/api/manualreport/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ManualReportController {

  @Resource
  private ManualReportService manualReportService;

  @PostMapping("/initManualReport")
  @ResponseBody
  public Response initManualReport(@Valid @RequestBody InitManualReportRequestType request) {
    if (StringUtils.isEmpty(request.getReportName())) {
      request.setReportName("Report" + System.currentTimeMillis());
    }
    InitManualReportResponseType response = manualReportService.initManualReport(request);
    return ResponseUtils.successResponse(response);
  }

  @PostMapping("/queryReportCases")
  @ResponseBody
  public Response queryReportCases(@RequestBody QueryReportCasesRequestType request) {
    QueryReportCasesResponseType response = new QueryReportCasesResponseType();
    response.setReportCases(manualReportService.queryReportCases(request));
    return ResponseUtils.successResponse(response);
  }
}
