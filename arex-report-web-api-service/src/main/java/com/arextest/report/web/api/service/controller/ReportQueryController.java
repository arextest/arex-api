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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


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
    public Response pushCompareResults(@Valid @RequestBody PushCompareResultsRequestType request) {
        PushCompareResultsResponseType response = new PushCompareResultsResponseType();
        response.setSuccess(reportService.saveCompareResults(request));
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/init")
    @ResponseBody
    public Response reportInitial(@RequestBody ReportInitialRequestType request) {
        if (request == null) {
            return ResponseUtils.requestBodyEmptyResponse();
        }
        ReportInitialResponseType response = new ReportInitialResponseType();
        response.setSuccess(replayInfoService.initPlan(request));
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/pushReplayStatus")
    @ResponseBody
    public Response changeReplayStatus(@Valid @RequestBody ChangeReplayStatusRequestType request) {
        ChangeReplayStatusResponseType response = new ChangeReplayStatusResponseType();
        response.setUpdateSuccess(reportService.changeReportStatus(request));
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/queryPlanStatistics")
    @ResponseBody
    public Response queryPlanStatistics(@RequestBody QueryPlanStatisticsRequestType request) {
        if (request == null || !request.checkPaging()) {
            return ResponseUtils.errorResponse("invalid paging parameter",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        QueryPlanStatisticsResponseType response = queryPlanStatisticsService.planStatistic(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/queryPlanItemStatistics")
    @ResponseBody
    public Response queryPlanItemStatistics(@RequestBody QueryPlanItemStatisticsRequestType request) {
        if (request == null || (request.getPlanId() == null && request.getPlanItemId() == null)) {
            return ResponseUtils.requestBodyEmptyResponse();
        }
        QueryPlanItemStatisticsResponseType response = queryPlanItemStatisticService.planItemStatistic(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/queryResponseTypeStatistic")
    @ResponseBody
    public Response queryResponseTypeStatistic(@Valid @RequestBody QueryCategoryStatisticRequestType request) {
        QueryCategoryStatisticResponseType response =
                queryResponseTypeStatisticService.categoryStatistic(request);
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/queryReplayCase")
    @ResponseBody
    public Response queryReplayCase(@Valid @RequestBody QueryReplayCaseRequestType request) {
        if (request == null || !request.checkPaging()) {
            return ResponseUtils.errorResponse("invalid paging parameter",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        QueryReplayCaseResponseType response = queryReplayCaseService.replayCaseStatistic(request);
        return ResponseUtils.successResponse(response);
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
    public Response queryDifferences(@Valid @RequestBody QueryDifferencesRequestType request) {
        QueryDifferencesResponseType response = diffSceneService.queryDifferences(request);
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/queryScenes")
    @ResponseBody
    public Response queryScenes(@Valid @RequestBody QueryScenesRequestType request) {
        QueryScenesResponseType response = diffSceneService.queryScenesByPage(request);
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/queryFullLinkMsg")
    @ResponseBody
    public Response queryFullLinkMsg(@Valid @RequestBody QueryFullLinkMsgRequestType request) {
        QueryFullLinkMsgResponseType response = queryReplayMsgService.queryFullLinkMsg(request);
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/queryReplayMsg")
    @ResponseBody
    public Response queryReplayMsg(@Valid @RequestBody QueryReplayMsgRequestType request) {
        QueryReplayMsgResponseType response = queryReplayMsgService.queryReplayMsg(request);
        return ResponseUtils.successResponse(response);
    }


    @PostMapping("/downloadReplayMsg")
    @ResponseBody
    public void downloadReplayMsg(@Valid @RequestBody DownloadReplayMsgRequestType request, HttpServletResponse response) {
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
