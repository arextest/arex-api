// package com.arextest.web.api.service.controller;
//
// import com.arextest.common.model.response.Response;
// import com.arextest.common.utils.ResponseUtils;
// import com.arextest.web.core.business.compare.CompareService;
// import com.arextest.web.model.contract.contracts.compare.AggCompareRequestType;
// import com.arextest.web.model.contract.contracts.compare.CaseCompareRequestType;
// import com.arextest.web.model.contract.contracts.compare.CaseCompareResponseType;
// import com.arextest.web.model.contract.contracts.compare.QuickCompareRequestType;
// import com.arextest.web.model.contract.contracts.compare.QuickCompareResponseType;
// import com.arextest.web.model.contract.contracts.compare.SendExceptionRequestType;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.*;
//
// import jakarta.annotation.Resource;
//
// /**
// * Created by rchen9 on 2022/6/29.
// */
// @Slf4j
// @Controller
// @RequestMapping("/api/compare/")
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class CompareController {
//
// @Resource
// CompareService compareService;
//
// // Comparison of a single case
// @PostMapping("/quickCompare")
// @ResponseBody
// public Response quickCompare(@RequestBody QuickCompareRequestType request) {
// QuickCompareResponseType response = compareService.quickCompareCompressMsg(request.getMsgCombination());
// return ResponseUtils.successResponse(response);
// }
//
// // Aggregate comparison of multiple cases
// @Deprecated
// @PostMapping("/aggCompare")
// @ResponseBody
// public Response aggCompare(@RequestBody AggCompareRequestType request) {
// compareService.aggCompare(request.getMsgCombinations());
// return ResponseUtils.successResponse(null);
// }
//
// // exception handler
// @PostMapping("/sendException")
// @ResponseBody
// public Response sendException(@RequestBody SendExceptionRequestType request) {
// compareService.sendException( request.getExceptionMsgs());
// return ResponseUtils.successResponse(null);
// }
//
// @PostMapping("/caseCompare")
// @ResponseBody
// public Response caseCompare(@RequestBody CaseCompareRequestType request){
// CaseCompareResponseType response = compareService.caseCompare(request.getMsgCombination());
// return ResponseUtils.successResponse(response);
// }
// }
