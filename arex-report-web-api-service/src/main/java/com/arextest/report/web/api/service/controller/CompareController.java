package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.compare.CompareService;
import com.arextest.report.model.api.contracts.compare.AggCompareResponseType;
import com.arextest.report.model.api.contracts.compare.QuickCompareRequestType;
import com.arextest.report.model.api.contracts.compare.QuickCompareResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by rchen9 on 2022/6/29.
 */
@Slf4j
@Controller
@RequestMapping("/api/compare/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CompareController {

    @Resource
    CompareService compareService;

    // Comparison of a single case
    @PostMapping("/quickCompare")
    @ResponseBody
    public Response quickCompare(@RequestBody QuickCompareRequestType request) {
        QuickCompareResponseType response = compareService.quickCompare(request.getMsgCombination());
        return ResponseUtils.successResponse(response);
    }

    // Aggregate comparison of multiple cases
    @PostMapping("/aggCompare")
    @ResponseBody
    public Response aggCompare(@RequestBody QuickCompareRequestType request) {
        AggCompareResponseType response = null;
        return ResponseUtils.successResponse(response);
    }
}
