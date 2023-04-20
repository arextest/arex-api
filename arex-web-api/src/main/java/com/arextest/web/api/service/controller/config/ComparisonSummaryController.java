package com.arextest.web.api.service.controller.config;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.config.replay.ComparisonSummaryService;
import com.arextest.web.model.contract.contracts.config.replay.ComparisonSummaryConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by rchen9 on 2023/1/9.
 */
@Controller
@RequestMapping("/api/config/comparison/summary")
public class ComparisonSummaryController {

    @Resource
    ComparisonSummaryService comparisonSummaryService;

    @RequestMapping("/queryByInterfaceId")
    @ResponseBody
    public final Response queryByInterfaceId(@RequestParam String interfaceId) {
        if (StringUtils.isEmpty(interfaceId)) {
            return InvalidResponse.REQUESTED_INTERFACE_ID_IS_EMPTY;
        }
        ComparisonSummaryConfiguration comparisonDetailsSummary =
                comparisonSummaryService.getComparisonDetailsSummary(interfaceId);

        return ResponseUtils.successResponse(comparisonDetailsSummary);
    }

}
