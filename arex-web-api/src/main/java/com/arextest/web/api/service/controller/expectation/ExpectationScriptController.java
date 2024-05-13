package com.arextest.web.api.service.controller.expectation;

import com.arextest.common.model.response.Response;
import com.arextest.common.model.response.ResponseCode;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.web.core.business.expectation.ExpectationScriptService;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptDeleteRequest;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptModel;
import com.arextest.web.model.contract.contracts.expectation.ExpectationScriptQueryRequest;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/expectation")
public class ExpectationScriptController {

    private final ExpectationScriptService service;

    public ExpectationScriptController(ExpectationScriptService service) {
        this.service = service;
    }

    @RequestMapping(path = "/query", method = RequestMethod.POST)
    @ResponseBody
    public Response query(@Valid @RequestBody ExpectationScriptQueryRequest request) {
        return ResponseUtils.successResponse(service.query(request));
    }

    @RequestMapping(path = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Response save(@Valid @RequestBody ExpectationScriptModel request) {
        boolean result = service.save(request);
        if (result) {
            return ResponseUtils.successResponse(true);
        }
        if (CollectionUtils.isEmpty(request.getExtractOperationList())) {
            return ResponseUtils.parameterInvalidResponse(
                "extract arex category/operation failed from expectation script");
        }
        if (CollectionUtils.isNotEmpty(request.getInvalidExtractAssertList())) {
            String originalText = request.getInvalidExtractAssertList().get(0).getOriginalText();
            return ResponseUtils.parameterInvalidResponse("invalid assert: " + originalText);
        }
        return ResponseUtils.errorResponse("save failed", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Response delete(@Valid @RequestBody ExpectationScriptDeleteRequest request) {
        boolean result = service.delete(request);
        if (result) {
            return ResponseUtils.successResponse(true);
        }
        return ResponseUtils.errorResponse("delete failed", ResponseCode.REQUESTED_HANDLE_EXCEPTION);
    }
}
