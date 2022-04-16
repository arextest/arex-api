package io.arex.report.web.api.service.controller;

import io.arex.common.model.response.Response;
import io.arex.common.model.response.ResponseCode;
import io.arex.common.utils.ResponseUtils;
import io.arex.report.core.business.dashboard.DashboardSummaryService;
import io.arex.report.model.api.contracts.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/api/dashboard/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @Resource
    DashboardSummaryService dashboardSummaryService;

    
    @PostMapping("/summary")
    @ResponseBody
    public Response dashboardSummary(@RequestBody DashboardSummaryRequestType request) {
        DashboardSummaryResponseType response = null;
        if (StringUtils.isNotEmpty(request.getAppId())) {
            response = dashboardSummaryService.getDashboardSummaryByAppId(request.getAppId());
        } else {
            response = dashboardSummaryService.getDashboardSummary();
        }
        if (response != null) {
            return ResponseUtils.successResponse(response);
        }
        return ResponseUtils.errorResponse("", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
    }

    
    @PostMapping("/queryAllAppId")
    @ResponseBody
    public Response queryAllAppId() {
        QueryAllAppIdResponseType response = new QueryAllAppIdResponseType();
        List<String> appIds = dashboardSummaryService.getAllAppId();
        response.setAppIds(appIds);
        return ResponseUtils.successResponse(response);
    }

    
    @PostMapping("/allAppResults")
    @ResponseBody
    public Response allAppResults(@RequestBody DashboardAllAppResultsRequestType request) {
        DashboardAllAppResultsResponseType response = dashboardSummaryService.allAppResults(request);
        return ResponseUtils.successResponse(response);
    }

    
    @PostMapping("/allAppDailyResults")
    @ResponseBody
    public Response allAppDailyResults(@RequestBody DashboardAllAppDailyResultsRequestType request) {
        DashboardAllAppDailyResultsResponseType response = dashboardSummaryService.allAppDailyResults(request);
        return ResponseUtils.successResponse(response);

    }
}
