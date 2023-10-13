package com.arextest.web.api.service.controller;

import com.arextest.common.annotation.AppAuth;
import com.arextest.common.context.ArexContext;
import com.arextest.common.enums.AuthRejectStrategy;
import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.model.replay.ViewRecordRequestType;
import com.arextest.web.common.HttpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wildeslam.
 * @create 2023/10/12 20:18
 */
@Controller
@RequestMapping("/api/replay/query")
public class ReplayQueryController {

    @Value("${arex.storage.viewRecord.url}")
    private String viewRecordUrl;

    @ResponseBody
    @GetMapping(value = "/viewRecord/")
    public Response viewRecord(String recordId,
                               @RequestParam(required = false) String category,
                               @RequestParam(required = false, defaultValue = "Rolling") String srcProvider) {
        ViewRecordRequestType recordRequestType = new ViewRecordRequestType();
        recordRequestType.setRecordId(recordId);
        recordRequestType.setSourceProvider(srcProvider);
        recordRequestType.setCategoryType(category);
        return viewRecord(recordRequestType);
    }

    @PostMapping("/viewRecord")
    @ResponseBody
    @AppAuth(rejectStrategy = AuthRejectStrategy.DOWNGRADE)
    public Response viewRecord(@RequestBody ViewRecordRequestType requestType) {
        ArexContext arexContext = new ArexContext();
        Map<String, String> headers = new HashMap<>();
        boolean downgrade = Boolean.FALSE.equals(arexContext.getPassAuth());
        headers.put("downgrade", Boolean.toString(downgrade));
        ResponseEntity<Response> response = HttpUtils.post(viewRecordUrl, requestType, Response.class, headers);
        if (response == null || response.getBody() == null) {
            return ResponseUtils.errorResponse("call storage failed", ResponseCode.REQUESTED_RESOURCE_NOT_FOUND);
        }
        return ResponseUtils.successResponse(response.getBody());
    }
}
