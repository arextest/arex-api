package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.ManualReportService;
import com.arextest.report.model.api.contracts.manualreport.InitManualReportRequestType;
import com.arextest.report.model.api.contracts.manualreport.InitManualReportResponseType;
import com.arextest.report.model.api.contracts.manualreport.QueryReportCasesRequestType;
import com.arextest.report.model.api.contracts.manualreport.QueryReportCasesResponseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("/api/manualreport/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ManualReportController {

    @Resource
    private ManualReportService manualReportService;

    @PostMapping("/initManualReport")
    @ResponseBody
    public Response initManualReport(@RequestBody InitManualReportRequestType request) {
        if (StringUtils.isEmpty(request.getWorkspaceId())) {
            return ResponseUtils.errorResponse("workspace is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getReportName())) {
            request.setReportName("Report" + System.currentTimeMillis());
        }
        try {
            InitManualReportResponseType response = manualReportService.initManualReport(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }

    @PostMapping("/queryReportCases")
    @ResponseBody
    public Response queryReportCases(@RequestBody QueryReportCasesRequestType request) {
        try {
            QueryReportCasesResponseType response = new QueryReportCasesResponseType();
            response.setReportCases(manualReportService.queryReportCases(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }
}
