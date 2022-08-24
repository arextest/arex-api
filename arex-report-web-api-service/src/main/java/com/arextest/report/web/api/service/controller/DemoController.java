package com.arextest.report.web.api.service.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.report.core.business.DemoService;
import com.arextest.report.core.business.ReportService;
import com.arextest.report.core.business.SceneService;
import com.arextest.report.model.api.contracts.PushCompareResultsRequestType;
import com.arextest.report.model.api.contracts.PushCompareResultsResponseType;
import com.arextest.report.model.api.contracts.QueryCompareResultsByPageRequestType;
import com.arextest.report.model.api.contracts.QueryCompareResultsByPageResponseType;
import com.arextest.report.model.api.contracts.common.CompareResult;
import com.arextest.report.model.api.contracts.common.LogEntity;
import com.arextest.report.model.dto.CompareResultDto;
import com.arextest.report.model.enums.DiffResultCode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Controller
@RequestMapping("/api/demo/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DemoController {
    @Resource
    private ReportService reportService;
    @Resource
    private DemoService demoService;
    @Resource
    private ObjectMapper objectMapper;

    @PostMapping("/saveCompareResultsMock")
    @ResponseBody
    public Response saveCompareResultsMock() {
        PushCompareResultsRequestType request = new PushCompareResultsRequestType();
        List<CompareResult> results = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            CompareResult cr = new CompareResult();
            cr.setPlanId(String.valueOf(i % 2));
            cr.setPlanItemId(String.valueOf(i % 5));
            cr.setReplayId("replayid" + i);
            cr.setDiffResultCode(DiffResultCode.COMPARED_INTERNAL_EXCEPTION);
            results.add(cr);
        }

        request.setResults(results);

        PushCompareResultsResponseType response = new PushCompareResultsResponseType();
        reportService.saveCompareResults(request);
        return ResponseUtils.successResponse(response);
    }

    @Resource
    private SceneService sceneService;

    @PostMapping("/calculateSceneMock")
    @ResponseBody
    public Response calculateScenesMock() {
        CompareResultDto dto = new CompareResultDto();
        List<LogEntity> logs = null;
        ClassPathResource classPathResource = new ClassPathResource("logs.txt");
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, LogEntity.class);
        try {
            InputStream inputStream = classPathResource.getInputStream();
            logs = objectMapper.readValue(inputStream, type);
        } catch (Exception e) {
            return ResponseUtils.errorResponse(e.getMessage(), ResponseCode.REQUESTED_HANDLE_EXCEPTION);
        }
        dto.setPlanItemId("332");
        dto.setPlanId("15");
        dto.setLogs(logs);
        dto.setReplayId("replayid");
        dto.setCategoryName("SOA");
        dto.setDiffResultCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);
        dto.setBaseMsg("baseMsg");
        dto.setTestMsg("testMsg");
        dto.setServiceName("serviceName");
        dto.setOperationName("operationName");
        dto.setOperationId("333");
        dto.setId("61d7f021ab26e550cfde8223");

        sceneService.statisticScenes(dto);

        return ResponseUtils.successResponse(true);
    }


    @PostMapping("/pagingDemo")
    @ResponseBody
    public Response queryCompareResultsByPage(@RequestBody QueryCompareResultsByPageRequestType request) {
        if (!request.checkPaging()) {
            return ResponseUtils.errorResponse("invalid paging parameter",
                    ResponseCode.REQUESTED_PARAMETER_INVALID);
        }
        QueryCompareResultsByPageResponseType response = demoService.queryCompareResultsByPage(request);
        return ResponseUtils.successResponse(response);
    }
}
