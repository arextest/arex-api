package com.arextest.web.api.service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arextest.common.annotation.AppAuth;
import com.arextest.common.context.ArexContext;
import com.arextest.common.enums.AuthRejectStrategy;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.model.replay.ViewRecordRequestType;
import com.arextest.model.response.Response;
import com.arextest.model.response.ResponseStatusType;
import com.arextest.web.common.HttpUtils;
import com.arextest.web.model.contract.contracts.ViewRecordResponseType;

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
    public Response viewRecord(String recordId, @RequestParam(required = false) String category,
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
        ArexContext arexContext = ArexContext.getContext();
        Map<String, String> headers = new HashMap<>();
        boolean downgrade = Boolean.FALSE.equals(arexContext.getPassAuth());
        headers.put("downgrade", Boolean.toString(downgrade));
        ResponseEntity<ViewRecordResponseType> response =
            HttpUtils.post(viewRecordUrl, requestType, ViewRecordResponseType.class, headers);
        ViewRecordResponseType responseType = new ViewRecordResponseType();
        ResponseStatusType responseStatusType = new ResponseStatusType();
        responseStatusType.setTimestamp(System.currentTimeMillis());
        if (response == null || response.getBody() == null) {
            responseStatusType.setResponseDesc("call storage failed");
            responseStatusType.setResponseCode(ResponseCode.REQUESTED_RESOURCE_NOT_FOUND.getCodeValue());
            responseType.setResponseStatusType(responseStatusType);
            return responseType;
        }

        responseStatusType.setResponseDesc("success");
        responseStatusType.setResponseCode(ResponseCode.SUCCESS.getCodeValue());
        responseType = response.getBody();
        responseType.setResponseStatusType(responseStatusType);
        return response.getBody();
    }
}
