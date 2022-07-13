package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.DiffSceneService;
import com.arextest.report.core.business.MsgShowService;
import com.arextest.report.core.business.QueryPlanItemStatisticService;
import com.arextest.report.core.business.QueryPlanStatisticsService;
import com.arextest.report.core.business.QueryReplayCaseService;
import com.arextest.report.core.business.QueryReplayMsgService;
import com.arextest.report.core.business.QueryResponseTypeStatisticService;
import com.arextest.report.core.business.ReplayInfoService;
import com.arextest.report.core.business.ReportService;
import com.arextest.report.core.business.SchemaInferService;
import com.arextest.report.model.api.contracts.ChangeReplayStatusRequestType;
import com.arextest.report.model.api.contracts.ChangeReplayStatusResponseType;
import com.arextest.report.model.api.contracts.DownloadReplayMsgRequestType;
import com.arextest.report.model.api.contracts.PushCompareResultsRequestType;
import com.arextest.report.model.api.contracts.PushCompareResultsResponseType;
import com.arextest.report.model.api.contracts.QueryCategoryStatisticRequestType;
import com.arextest.report.model.api.contracts.QueryCategoryStatisticResponseType;
import com.arextest.report.model.api.contracts.QueryDiffAggInfoRequestType;
import com.arextest.report.model.api.contracts.QueryDiffAggInfoResponseType;
import com.arextest.report.model.api.contracts.QueryDifferencesRequestType;
import com.arextest.report.model.api.contracts.QueryDifferencesResponseType;
import com.arextest.report.model.api.contracts.QueryFullLinkMsgRequestType;
import com.arextest.report.model.api.contracts.QueryFullLinkMsgResponseType;
import com.arextest.report.model.api.contracts.QueryMsgSchemaRequestType;
import com.arextest.report.model.api.contracts.QueryMsgSchemaResponseType;
import com.arextest.report.model.api.contracts.QueryMsgShowByCaseRequestType;
import com.arextest.report.model.api.contracts.QueryMsgShowByCaseResponseType;
import com.arextest.report.model.api.contracts.QueryMsgWithDiffRequestType;
import com.arextest.report.model.api.contracts.QueryMsgWithDiffResponseType;
import com.arextest.report.model.api.contracts.QueryPlanItemStatisticsRequestType;
import com.arextest.report.model.api.contracts.QueryPlanItemStatisticsResponseType;
import com.arextest.report.model.api.contracts.QueryPlanStatisticsRequestType;
import com.arextest.report.model.api.contracts.QueryPlanStatisticsResponseType;
import com.arextest.report.model.api.contracts.QueryReplayCaseRequestType;
import com.arextest.report.model.api.contracts.QueryReplayCaseResponseType;
import com.arextest.report.model.api.contracts.QueryReplayMsgRequestType;
import com.arextest.report.model.api.contracts.QueryReplayMsgResponseType;
import com.arextest.report.model.api.contracts.QueryScenesRequestType;
import com.arextest.report.model.api.contracts.QueryScenesResponseType;
import com.arextest.report.model.api.contracts.QuerySchemaForConfigRequestType;
import com.arextest.report.model.api.contracts.ReportInitialRequestType;
import com.arextest.report.model.api.contracts.ReportInitialResponseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Controller
@RequestMapping("/api/report/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportQueryController {
    @Resource
    private ReportService reportService;
    @Resource
    private ReplayInfoService replayInfoService;
    @Resource
    private QueryPlanStatisticsService queryPlanStatisticsService;
    @Resource
    private QueryPlanItemStatisticService queryPlanItemStatisticService;
    @Resource
    private QueryResponseTypeStatisticService queryResponseTypeStatisticService;
    @Resource
    private DiffSceneService diffSceneService;
    @Resource
    private MsgShowService msgShowService;
    @Resource
    private QueryReplayCaseService queryReplayCaseService;
    @Resource
    private QueryReplayMsgService queryReplayMsgService;
    @Resource
    private SchemaInferService schemaInferService;


    @PostMapping("/pushCompareResults")
    @ResponseBody
    public Response pushCompareResults(@RequestBody PushCompareResultsRequestType request) {
        if (request.getResults() == null) {
            return ResponseUtils.errorResponse("results is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            PushCompareResultsResponseType response = new PushCompareResultsResponseType();
            response.setSuccess(reportService.saveCompareResults(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse("failed to save compare results",
                    ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }


    @PostMapping("/init")
    @ResponseBody
    public Response reportInitial(@RequestBody ReportInitialRequestType request) {
        if (request == null) {
            return ResponseUtils.requestBodyEmptyResponse();
        }
        try {
            ReportInitialResponseType response = new ReportInitialResponseType();
            response.setSuccess(replayInfoService.initPlan(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            return ResponseUtils.errorResponse("failed to init replay info", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }


    @PostMapping("/pushReplayStatus")
    @ResponseBody
    public Response changeReplayStatus(@RequestBody ChangeReplayStatusRequestType request) {
        if (request.getPlanId() == null) {
            return ResponseUtils.errorResponse("planId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            ChangeReplayStatusResponseType response = new ChangeReplayStatusResponseType();
            response.setUpdateSuccess(reportService.changeReportStatus(request));
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            LOGGER.error("failed to update replay status, error:{},request:{}", e.toString(), request);
            return ResponseUtils.exceptionResponse(e.toString());
        }
    }


    @PostMapping("/queryPlanStatistics")
    @ResponseBody
    public Response queryPlanStatistics(@RequestBody QueryPlanStatisticsRequestType request) {
        if (request == null || !request.checkPaging()) {
            return ResponseUtils.errorResponse("invalid paging parameter",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            QueryPlanStatisticsResponseType response = queryPlanStatisticsService.planStatistic(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            LOGGER.error("queryPlanStatistics exception, error:{},request:{}", e.toString(), request);
            return ResponseUtils.exceptionResponse(e.toString());
        }
    }

    @PostMapping("/queryPlanItemStatistics")
    @ResponseBody
    public Response queryPlanItemStatistics(@RequestBody QueryPlanItemStatisticsRequestType request) {
        if (request == null || (request.getPlanId() == null && request.getPlanItemId() == null)) {
            return ResponseUtils.requestBodyEmptyResponse();
        }
        try {
            QueryPlanItemStatisticsResponseType response = queryPlanItemStatisticService.planItemStatistic(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            LOGGER.error("planItemStatistics exception, error:{},request:{}", e.toString(), request);
            return ResponseUtils.exceptionResponse(e.toString());
        }
    }

    @PostMapping("/queryResponseTypeStatistic")
    @ResponseBody
    public Response queryResponseTypeStatistic(@RequestBody QueryCategoryStatisticRequestType request) {
        if (request == null || request.getPlanItemId() == null) {
            return ResponseUtils.errorResponse("planItemId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            QueryCategoryStatisticResponseType response =
                    queryResponseTypeStatisticService.categoryStatistic(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            LOGGER.error("responseTypeStatistic exception, error:{},request:{}", e.toString(), request);
            return ResponseUtils.exceptionResponse(e.toString());
        }
    }

    @PostMapping("/queryReplayCase")
    @ResponseBody
    public Response queryReplayCase(@RequestBody QueryReplayCaseRequestType request) {
        if (request == null || !request.checkPaging()) {
            return ResponseUtils.errorResponse("invalid paging parameter",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }

        if (request.getPlanItemId() == null) {
            return ResponseUtils.errorResponse("planItemId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            QueryReplayCaseResponseType response = queryReplayCaseService.replayCaseStatistic(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            LOGGER.error("queryReplayCase exception, error:{},request:{}", e.toString(), request);
            return ResponseUtils.exceptionResponse(e.toString());
        }
    }

    @PostMapping("/queryDiffAggInfo")
    @ResponseBody
    public Response queryDiffAggInfo(@RequestBody QueryDiffAggInfoRequestType request) {
        QueryDiffAggInfoResponseType response = new QueryDiffAggInfoResponseType();
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/queryMsgWithDiff")
    @ResponseBody
    public Response queryMsgWithDiff(@RequestBody QueryMsgWithDiffRequestType request) {
        QueryMsgWithDiffResponseType response = null;
        try {
            response = msgShowService.queryMsgWithDiff(request);
        } catch (JSONException e) {
            LOGGER.error("queryMsgWithDiff", e);
        }
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/queryMsgShowByCase")
    @ResponseBody
    public Response queryMsgShowByCase(@RequestBody QueryMsgShowByCaseRequestType request) {
        QueryMsgShowByCaseResponseType response = new QueryMsgShowByCaseResponseType();
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/queryDifferences")
    @ResponseBody
    public Response queryDifferences(@RequestBody QueryDifferencesRequestType request) {
        if (request.getPlanItemId() == null) {
            return ResponseUtils.errorResponse("planItemId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (request.getCategoryName() == null) {
            return ResponseUtils.errorResponse("categoryName is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (StringUtils.isEmpty(request.getOperationName())) {
            return ResponseUtils.errorResponse("operationName is empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            QueryDifferencesResponseType response = diffSceneService.queryDifferences(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            LOGGER.error("query difference failed.", e);
            return ResponseUtils.errorResponse("failed to get differences", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }


    @PostMapping("/queryScenes")
    @ResponseBody
    public Response queryScenes(@RequestBody QueryScenesRequestType request) {
        if (StringUtils.isEmpty(request.getOperationName())) {
            return ResponseUtils.errorResponse("operationName is empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (request.getPlanItemId() == null) {
            return ResponseUtils.errorResponse("planItemId is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        if (request.getCategoryName() == null) {
            return ResponseUtils.errorResponse("categoryName is empty", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        try {
            QueryScenesResponseType response = diffSceneService.queryScenesByPage(request);
            return ResponseUtils.successResponse(response);
        } catch (Exception e) {
            LOGGER.error("failed to get scenes by difference", e);
            return ResponseUtils.errorResponse("failed to get scenes by difference",
                    ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
    }


    @PostMapping("/queryFullLinkMsg")
    @ResponseBody
    public Response queryFullLinkMsg(@RequestBody QueryFullLinkMsgRequestType request) {
        if (StringUtils.isEmpty(request.getRecordId())) {
            return ResponseUtils.errorResponse("id is empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        QueryFullLinkMsgResponseType response = queryReplayMsgService.queryFullLinkMsg(request);
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/queryReplayMsg")
    @ResponseBody
    public Response queryReplayMsg(@RequestBody QueryReplayMsgRequestType request) {
        if (StringUtils.isEmpty(request.getId())) {
            return ResponseUtils.errorResponse("queryReplayMsg id is empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        QueryReplayMsgResponseType response = queryReplayMsgService.queryReplayMsg(request);
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/downloadReplayMsg")
    @ResponseBody
    public void downloadReplayMsg(@RequestBody DownloadReplayMsgRequestType request, HttpServletResponse response) {
        if (StringUtils.isEmpty(request.getId())) {
            LOGGER.error("downloadReplayMsg id is empty");
        }
        queryReplayMsgService.downloadReplayMsg(request, response);
    }


    @PostMapping("/queryMsgSchema")
    @ResponseBody
    public Response queryMsgSchema(@RequestBody QueryMsgSchemaRequestType request) {
        if (StringUtils.isEmpty(request.getId()) && StringUtils.isEmpty(request.getMsg())) {
            return ResponseUtils.errorResponse("queryMsgSchema id is empty",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        QueryMsgSchemaResponseType response = schemaInferService.schemaInfer(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/querySchemaForConfig")
    @ResponseBody
    public Response querySchemaForConfig(@RequestBody QuerySchemaForConfigRequestType request) {
        QueryMsgSchemaResponseType response = new QueryMsgSchemaResponseType();
        if (StringUtils.isNotEmpty(request.getMsg())) {
            response = schemaInferService.schemaInferForConfig(request);
        }
        return ResponseUtils.successResponse(response);
    }
}
