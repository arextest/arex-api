package com.arextest.web.api.service.controller;

import com.arextest.common.annotation.AppAuth;
import com.arextest.common.context.ArexContext;
import com.arextest.common.enums.AuthRejectStrategy;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.model.mock.AREXMocker;
import com.arextest.model.mock.Mocker;
import com.arextest.model.replay.ViewRecordRequestType;
import com.arextest.model.response.Response;
import com.arextest.model.response.ResponseStatusType;
import com.arextest.web.common.HttpUtils;
import com.arextest.web.core.business.casedetail.CaseDetailMockerProcessor;
import com.arextest.web.model.contract.contracts.casedetail.CaseDetailResponse;
import com.arextest.web.model.contract.contracts.casedetail.ViewRecordResponseType;
import java.util.HashMap;
import java.util.List;
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

/**
 * @author wildeslam.
 * @create 2023/10/12 20:18
 */
@Controller
@RequestMapping("/api/replay/query")
public class ReplayQueryController {

  @Value("${arex.storage.view.record.url}")
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

    // original response from arex-storage
    ResponseEntity<ViewRecordResponseType> storageRes =
        HttpUtils.post(viewRecordUrl, requestType, ViewRecordResponseType.class, headers);

    CaseDetailResponse res = new CaseDetailResponse();
    ResponseStatusType status = new ResponseStatusType();
    status.setTimestamp(System.currentTimeMillis());

    if (storageRes == null || storageRes.getBody() == null) {
      status.setResponseDesc("call storage failed");
      status.setResponseCode(ResponseCode.REQUESTED_RESOURCE_NOT_FOUND.getCodeValue());
      res.setResponseStatusType(status);
    } else {
      status.setResponseDesc("success");
      status.setResponseCode(ResponseCode.SUCCESS.getCodeValue());
      res.setResponseStatusType(status);

      List<AREXMocker> mockers = storageRes.getBody().getRecordResult();
      res.setRecordResult(CaseDetailMockerProcessor.convertMocker(mockers));
    }
    return res;
  }
}
