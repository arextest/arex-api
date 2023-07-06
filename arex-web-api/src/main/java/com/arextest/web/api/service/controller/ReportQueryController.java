package com.arextest.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.common.LogUtils;
import com.arextest.web.core.business.DiffSceneService;
import com.arextest.web.core.business.MsgShowService;
import com.arextest.web.core.business.QueryPlanItemStatisticService;
import com.arextest.web.core.business.QueryPlanStatisticsService;
import com.arextest.web.core.business.QueryReplayCaseService;
import com.arextest.web.core.business.QueryReplayMsgService;
import com.arextest.web.core.business.QueryResponseTypeStatisticService;
import com.arextest.web.core.business.RecordService;
import com.arextest.web.core.business.ReplayInfoService;
import com.arextest.web.core.business.ReportService;
import com.arextest.web.core.business.SchemaInferService;
import com.arextest.web.core.business.iosummary.SceneReportService;
import com.arextest.web.model.contract.contracts.ChangeReplayStatusRequestType;
import com.arextest.web.model.contract.contracts.ChangeReplayStatusResponseType;
import com.arextest.web.model.contract.contracts.DownloadReplayMsgRequestType;
import com.arextest.web.model.contract.contracts.PushCompareResultsRequestType;
import com.arextest.web.model.contract.contracts.PushCompareResultsResponseType;
import com.arextest.web.model.contract.contracts.QueryCategoryStatisticRequestType;
import com.arextest.web.model.contract.contracts.QueryCategoryStatisticResponseType;
import com.arextest.web.model.contract.contracts.QueryDiffAggInfoRequestType;
import com.arextest.web.model.contract.contracts.QueryDiffAggInfoResponseType;
import com.arextest.web.model.contract.contracts.QueryDiffMsgByIdResponseType;
import com.arextest.web.model.contract.contracts.QueryDifferencesRequestType;
import com.arextest.web.model.contract.contracts.QueryDifferencesResponseType;
import com.arextest.web.model.contract.contracts.QueryFullLinkInfoResponseType;
import com.arextest.web.model.contract.contracts.QueryFullLinkMsgRequestType;
import com.arextest.web.model.contract.contracts.QueryFullLinkMsgResponseType;
import com.arextest.web.model.contract.contracts.QueryLogEntityRequestTye;
import com.arextest.web.model.contract.contracts.QueryMsgSchemaRequestType;
import com.arextest.web.model.contract.contracts.QueryMsgSchemaResponseType;
import com.arextest.web.model.contract.contracts.QueryMsgShowByCaseRequestType;
import com.arextest.web.model.contract.contracts.QueryMsgShowByCaseResponseType;
import com.arextest.web.model.contract.contracts.QueryMsgWithDiffRequestType;
import com.arextest.web.model.contract.contracts.QueryMsgWithDiffResponseType;
import com.arextest.web.model.contract.contracts.QueryPlanFailCaseRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanItemStatisticsRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanItemStatisticsResponseType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsRequestType;
import com.arextest.web.model.contract.contracts.QueryPlanStatisticsResponseType;
import com.arextest.web.model.contract.contracts.QueryReplayCaseRequestType;
import com.arextest.web.model.contract.contracts.QueryReplayCaseResponseType;
import com.arextest.web.model.contract.contracts.QueryReplayMsgRequestType;
import com.arextest.web.model.contract.contracts.QueryReplayMsgResponseType;
import com.arextest.web.model.contract.contracts.QuerySceneInfoResponseType;
import com.arextest.web.model.contract.contracts.QueryScenesRequestType;
import com.arextest.web.model.contract.contracts.QueryScenesResponseType;
import com.arextest.web.model.contract.contracts.QuerySchemaForConfigRequestType;
import com.arextest.web.model.contract.contracts.ReportInitialRequestType;
import com.arextest.web.model.contract.contracts.ReportInitialResponseType;
import com.arextest.web.model.contract.contracts.SuccessResponseType;
import com.arextest.web.model.contract.contracts.SyncResponseContractRequestType;
import com.arextest.web.model.contract.contracts.record.CountRecordRequestType;
import com.arextest.web.model.contract.contracts.record.ListRecordRequestType;
import com.arextest.web.model.contract.contracts.replay.AnalyzeCompareResultsRequestType;
import com.arextest.web.model.contract.contracts.replay.AnalyzeCompareResultsResponseType;
import com.arextest.web.model.contract.contracts.replay.UpdateReportInfoRequestType;
import com.arextest.web.model.contract.contracts.replay.UpdateReportInfoResponseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Resource
    private SceneReportService sceneReportService;
    @Resource
    private RecordService recordService;

    @Deprecated
    @PostMapping("/pushCompareResults")
    @ResponseBody
    public Response pushCompareResults(@Valid @RequestBody PushCompareResultsRequestType request) {
        PushCompareResultsResponseType response = new PushCompareResultsResponseType();
        response.setSuccess(reportService.saveCompareResults(request));
        return ResponseUtils.successResponse(response);
    }

    @PostMapping("/analyzeCompareResults")
    @ResponseBody
    public Response analyzeCompareResults(@Valid @RequestBody AnalyzeCompareResultsRequestType request) {
        AnalyzeCompareResultsResponseType response = new AnalyzeCompareResultsResponseType();
        response.setSuccess(reportService.analyzeCompareResults(request));
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

    @PostMapping("/updateReportInfo")
    @ResponseBody
    public Response updateReportInfo(@Valid @RequestBody UpdateReportInfoRequestType request) {
        UpdateReportInfoResponseType response = new UpdateReportInfoResponseType();
        response.setSuccess(replayInfoService.updatePlan(request));
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
            LogUtils.error(LOGGER, "queryMsgWithDiff", e);
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
    public void downloadReplayMsg(@Valid @RequestBody DownloadReplayMsgRequestType request,
                                  HttpServletResponse response) {
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

    @GetMapping("/delete/{planId}")
    @ResponseBody
    public Response deleteReport(@PathVariable String planId) {
        SuccessResponseType response = new SuccessResponseType();
        response.setSuccess(reportService.deleteReport(planId));
        return ResponseUtils.successResponse(response);
    }

    @GetMapping("/querySceneInfo/{planId}/{planItemId}")
    @ResponseBody
    public Response querySceneInfo(@PathVariable String planId, @PathVariable String planItemId) {
        QuerySceneInfoResponseType response = sceneReportService.querySceneInfo(planId, planItemId);
        return ResponseUtils.successResponse(response);
    }


    @GetMapping("/queryFullLinkInfo/{planItemId}/{recordId}")
    @ResponseBody
    public Response queryFullLinkInfo(@PathVariable String planItemId, @PathVariable String recordId) {
        QueryFullLinkInfoResponseType response =
                queryReplayMsgService.queryFullLinkInfo(planItemId, recordId);
        return ResponseUtils.successResponse(response);
    }

    @Deprecated
    @GetMapping("/queryDiffMsgById/{id}")
    @ResponseBody
    public Response queryDiffMsgById(@PathVariable String id) {
        QueryDiffMsgByIdResponseType response = queryReplayMsgService.queryDiffMsgById(id);
        return ResponseUtils.successResponse(response);
    }

    @Deprecated
    @PostMapping("/queryLogEntity")
    @ResponseBody
    public Response queryLogEntity(@Valid @RequestBody QueryLogEntityRequestTye request) {
        return ResponseUtils.successResponse(
                queryReplayMsgService.queryLogEntity(request)
        );
    }

    @PostMapping("/queryPlanFailCase")
    @ResponseBody
    public Response queryPlanFailCase(@Valid @RequestBody QueryPlanFailCaseRequestType request) {
        return ResponseUtils.successResponse(
                queryReplayCaseService.queryPlanFailCase(request)
        );
    }

    @PostMapping("/countRecord")
    @ResponseBody
    public Response countRecord(@Valid @RequestBody CountRecordRequestType requestType) {
        return ResponseUtils.successResponse(recordService.countRecord(requestType));
    }

    @PostMapping("/listRecord")
    @ResponseBody
    public Response countRecord(@Valid @RequestBody ListRecordRequestType requestType) {
        if (requestType.getOperationType() == null) {
            return ResponseUtils.errorResponse("no operationType", ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        return ResponseUtils.successResponse(recordService.listRecord(requestType));
    }

    @PostMapping("/aggCount")
    @ResponseBody
    public Response aggCountRecord(@Valid @RequestBody CountRecordRequestType requestType) {
        return ResponseUtils.successResponse(recordService.aggCountRecord(requestType));
    }

    @PostMapping("/syncResponseContract")
    @ResponseBody
    public Response syncResponse(@Valid @RequestBody SyncResponseContractRequestType requestType) {
        return ResponseUtils.successResponse(schemaInferService.syncResponseContract(requestType));
    }

    @GetMapping("/queryContract/{id}")
    @ResponseBody
    public Response queryContract(@PathVariable String id) {
        return ResponseUtils.successResponse(schemaInferService.queryContract(id));
    }
}
