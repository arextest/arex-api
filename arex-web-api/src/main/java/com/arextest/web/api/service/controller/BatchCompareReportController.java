// package com.arextest.web.api.service.controller;
//
// import com.arextest.common.model.response.Response;
// import com.arextest.common.utils.ResponseUtils;
// import com.arextest.web.core.business.BatchCompareReportService;
// import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareInterfaceProcess;
// import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareReportRequestType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.BatchCompareSummaryItem;
// import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareCaseMsgWithDiffRequestType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareCaseMsgWithDiffResponseType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareProgressRequestType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareProgressResponseType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareSummaryRequestType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.QueryBatchCompareSummaryResponseType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.QueryMoreDiffInSameCardRequestType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.QueryMoreDiffInSameCardResponseType;
// import com.arextest.web.model.contract.contracts.batchcomparereport.UpdateBatchCompareCaseRequestType;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseBody;
//
// import jakarta.validation.Valid;
// import java.util.List;
//
// /**
// * Created by rchen9 on 2023/2/7.
// */
// @Slf4j
// @Controller
// @RequestMapping("/api/batchcomparereport/")
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class BatchCompareReportController {
//
// @Autowired
// BatchCompareReportService batchCompareReportService;
//
// @PostMapping("/initBatchCompareReport")
// @ResponseBody
// public Response initBatchCompareReport(@Valid @RequestBody BatchCompareReportRequestType request) {
// return ResponseUtils.successResponse(
// batchCompareReportService.initBatchCompareReport(request)
// );
// }
//
// @PostMapping("/updateBatchCompareCase")
// @ResponseBody
// public Response updateBatchCompareCase(@Valid @RequestBody UpdateBatchCompareCaseRequestType request) {
// boolean result = batchCompareReportService.updateBatchCompareCase(request);
// return ResponseUtils.successResponse(result);
// }
//
// @PostMapping("/queryBatchCompareProgress")
// @ResponseBody
// public Response queryBatchCompareProgress(@Valid @RequestBody QueryBatchCompareProgressRequestType request) {
// QueryBatchCompareProgressResponseType response = new QueryBatchCompareProgressResponseType();
// List<BatchCompareInterfaceProcess> batchCompareInterfaceProcesses =
// batchCompareReportService.queryBatchCompareProgress(request);
// response.setBatchCompareInterfaceProcessList(batchCompareInterfaceProcesses);
// return ResponseUtils.successResponse(batchCompareInterfaceProcesses);
// }
//
// @PostMapping("/queryBatchCompareSummary")
// @ResponseBody
// public Response queryBatchCompareSummary(@Valid @RequestBody QueryBatchCompareSummaryRequestType request) {
// QueryBatchCompareSummaryResponseType response = new QueryBatchCompareSummaryResponseType();
// List<BatchCompareSummaryItem> batchCompareSummaryItems =
// batchCompareReportService.queryBatchCompareSummary(request);
// response.setBatchCompareSummaryItems(batchCompareSummaryItems);
// return ResponseUtils.successResponse(response);
// }
//
// @PostMapping("/queryBatchCompareCaseMsgWithDiff")
// @ResponseBody
// public Response queryBatchCompareCaseMsgWithDiff(@Valid @RequestBody QueryBatchCompareCaseMsgWithDiffRequestType
// request) {
// QueryBatchCompareCaseMsgWithDiffResponseType response =
// batchCompareReportService.queryBatchCompareCaseMsgWithDiff(request.getLogId());
// return ResponseUtils.successResponse(response);
// }
//
// @PostMapping("/queryMoreDiffInSameCard")
// @ResponseBody
// public Response queryMoreDiffInSameCard(@Valid @RequestBody QueryMoreDiffInSameCardRequestType request) {
// QueryMoreDiffInSameCardResponseType response =
// batchCompareReportService.queryMoreDiffInSameCard(request);
// return ResponseUtils.successResponse(response);
// }
//
// }
